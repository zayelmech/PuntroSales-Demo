package com.imecatro.demosales.ui.clients.list.views

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    onCardClicked: (Long?) -> Unit = {},
    onNavigateAction: () -> Unit = {},
    onSyncClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            if (isLoading)
                LinearProgressIndicator()
            else
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.top_bar_clients))
                    },
                    actions = {
//                    IconButton(onClick = {}) {
//                        Icon(Icons.Default.Search, contentDescription = "Sync Contacts")
//                    }
                        IconButton(onClick = onSyncClicked) {
                            Icon(Icons.Default.Sync, contentDescription = "Sync Contacts")
                        }
                    }
                )
        },
        floatingActionButton = {
            FloatingActionButton(onNavigateAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_client),
                    contentDescription = null
                )

            }
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = paddingValues
        ) {
            items(list) { client ->

                ClientCardCompose(client = client) { onCardClicked(client.id) }
            }
        }
    }
}

@Composable
fun ClientListImpl(
    clientsListViewModel: ClientsListViewModel,
    onNavigateAction: (Long?) -> Unit
) {
    val uiState by clientsListViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val contacts by clientsListViewModel.contacts.collectAsState()

    var showSyncOptions by remember { mutableStateOf(false) }
    var showContactSelection by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            clientsListViewModel.fetchContacts(context.contentResolver)
            showSyncOptions = true
        }
    }

    ListOfClients(
        list = uiState.clients,
        isLoading = uiState.isLoading,
        onCardClicked = { id -> onNavigateAction(id) },
        onNavigateAction = { onNavigateAction(null) },
        onSyncClicked = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) -> {
                    clientsListViewModel.fetchContacts(context.contentResolver)
                    showSyncOptions = true
                }

                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    )

    if (showSyncOptions) {
        SyncOptionsDialog(
            onDismissRequest = { showSyncOptions = false },
            onSyncAll = { clientsListViewModel.syncAllContacts() },
            onSelectSpecific = { showContactSelection = true }
        )
    }

    if (showContactSelection) {
        ContactSelectionDialog(
            contacts = contacts,
            onDismissRequest = { showContactSelection = false },
            onContactsSelected = { selected ->
                clientsListViewModel.syncSelectedContacts(selected)
            }
        )
    }
}
