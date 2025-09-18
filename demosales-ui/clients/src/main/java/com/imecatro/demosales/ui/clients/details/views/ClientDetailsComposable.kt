package com.imecatro.demosales.ui.clients.details.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.viewmodel.ClientDetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ClientDetailsComposable(
    clientDetails: ClientDetailsUiModel = ClientDetailsUiModel.dummy,
    onNavigateBack: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    val view = LocalView.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TopAppBar(title = { Text(text = "Client Details") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        null
                    )
                }
            })
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .placeholder(R.drawable.baseline_mood_24)
                            .error(R.drawable.baseline_mood_24)
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
                SelectionContainer {
                    Text(
                        text = clientDetails.phoneNumber,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                //Details
                Text(text = "Address", style = MaterialTheme.typography.titleSmall)
                HorizontalDivider()
                SelectionContainer {
                    Text(
                        text = clientDetails.clientAddress,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //button edit
                Button(
                    onClick = onEditClicked,
                    modifier = Modifier
                        .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(text = stringResource(R.string.btn_edit))
                }
                Spacer(modifier = Modifier.width(20.dp))
                //button delete
                TextButton(
                    onClick = onDeleteClicked,
                    modifier = Modifier.sizeIn(minWidth = 120.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = stringResource(R.string.btn_delete))
                }
            }
        }
    }
}


@Composable
fun ClientDetailsComposableImpl(
    clientDetailsViewModel: ClientDetailsViewModel,
    onBackToList: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {

    val uiState by clientDetailsViewModel.uiState.collectAsState()


    ClientDetailsComposable(
        clientDetails = uiState,
        onNavigateBack = onBackToList,
        onDeleteClicked = { clientDetailsViewModel.onDeleteClientAction(uiState.clientId) },
        onEditClicked = onEditClicked
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.isClientDeleted) {
            onBackToList()
        }
    }
}

