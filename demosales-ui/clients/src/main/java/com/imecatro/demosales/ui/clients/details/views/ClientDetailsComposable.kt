package com.imecatro.demosales.ui.clients.details.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.viewmodel.ClientDetailsViewModel
import com.imecatro.demosales.ui.theme.ButtonFancy


@Preview(showBackground = true)
@Composable
private fun ClientDetailsComposable(
    clientDetails: ClientDetailsUiModel = ClientDetailsUiModel.dummy,
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    val paddingX = 20.dp
    val view = LocalView.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {

            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter =
                if (view.isInEditMode)
                    painterResource(id = R.drawable.baseline_mood_24)
                else
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(clientDetails.imageUri)
                            .crossfade(true)
                            .build()
                    ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredSizeIn(maxHeight = 280.dp),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                //Client name
                Text(
                    text = clientDetails.clientName,
                    style = MaterialTheme.typography.titleMedium
                )

                //Phone Number
                Text(
                    text = clientDetails.phoneNumber,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                //Details
                Text(text = "Address", style = MaterialTheme.typography.titleSmall)
                HorizontalDivider()
                Text(
                    text = clientDetails.clientAddress,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
            //button edit
            ButtonFancy(
                text = "EDIT",
                color = MaterialTheme.colorScheme.primary,
                paddingX = paddingX,
                icon = Icons.Filled.Edit,
                onClicked = onEditClicked
            )
            //button delete
            ButtonFancy(
                text = "DELETE",
                color = MaterialTheme.colorScheme.secondary,
                paddingX = paddingX,
                icon = Icons.Filled.Delete,
                onClicked = onDeleteClicked
            )
        }
    }
}


@Composable
fun ClientDetailsComposableImpl(
    clientDetailsViewModel: ClientDetailsViewModel,
    onClientDeleted: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {

    val uiState by clientDetailsViewModel.uiState.collectAsState()


    ClientDetailsComposable(
        clientDetails = uiState,
        onDeleteClicked = { clientDetailsViewModel.onDeleteClientAction(uiState.clientId) },
        onEditClicked = onEditClicked
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.isClientDeleted) {
            onClientDeleted()
        }
    }
}

