package com.imecatro.demosales.ui.clients.list.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel
import com.imecatro.demosales.ui.clients.list.model.ClientsListPresenterModel
import com.imecatro.demosales.ui.clients.list.model.imageUrl
import com.imecatro.demosales.ui.clients.list.viewmodel.ClientsListViewModel


private val ClientsListPresenterModel.isLoading: Boolean
    get() {
        return isFetchingClients
    }


@Preview(showBackground = true)
@Composable
internal fun ClientCardCompose(
    client: ClientUiModel = ClientUiModel.getDummy(),
    onCardClicked: () -> Unit = {}
) {
    val view = LocalView.current
    Box(
        modifier = Modifier
            .clickable { onCardClicked() },
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            headlineContent = {
                Text(text = client.name ?: "")
            },
            leadingContent = {
                Image(
                    painter = if (view.isInEditMode)
                        painterResource(id = R.drawable.baseline_mood_24)
                    else rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(client.imageUrl)
                            .error(R.drawable.baseline_mood_24)
                            .crossfade(true)
                            .build()

                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(25)),
                    contentScale = ContentScale.FillWidth
                )
            },
            supportingContent = {
                Text(text = client.address ?: "")
            },
            overlineContent = {
                Text(text = client.number ?: "")
            }

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ListOfClients(
    list: List<ClientUiModel> = listOf(ClientUiModel.getDummy(), ClientUiModel.getDummy()),
    isLoading: Boolean = false,
    onCardClicked: (Int?) -> Unit = {},
    onNavigateAction: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(title = {
            Text(text = "Clients")
        }, actions = {

        })

        Scaffold(floatingActionButton = {
            FloatingActionButton(onNavigateAction) {
                Icon(painter = painterResource(R.drawable.ic_add_client), contentDescription = null)

            }
        }) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = paddingValues
            ) {
                items(list) { client ->

                    ClientCardCompose(client = client) { onCardClicked(client.id) }
                }
            }
        }

    }

}

@Composable
fun ClientListImpl(
    clientsListViewModel: ClientsListViewModel,
    onNavigateAction: (Int?) -> Unit
) {
    val uiState by clientsListViewModel.uiState.collectAsState()

    ListOfClients(
        list = uiState.clients,
        isLoading = uiState.isLoading,
        onCardClicked = { id -> onNavigateAction(id) },
        onNavigateAction = { onNavigateAction(null) }
    )


}