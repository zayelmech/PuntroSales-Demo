package com.imecatro.demosales.ui.clients.details.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import com.imecatro.demosales.ui.clients.details.viewmodel.ClientDetailsViewModel
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import kotlinx.coroutines.launch


@Composable
private fun ClientPurchasesComposable(
    purchases: List<PurchaseUiModel>
) {
    if (purchases.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.txt_no_purchases),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(purchases) { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "#${purchase.purchaseNumber}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = purchase.amount,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = purchase.date,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = purchase.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientDetailsInfoComposable(
    clientDetails: ClientDetailsUiModel,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(clientDetails.latitude, clientDetails.longitude),
            15f
        )
    }

    LaunchedEffect(clientDetails.latitude, clientDetails.longitude) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(clientDetails.latitude, clientDetails.longitude),
                15f
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            Text(
                text = stringResource(R.string.txt_address),
                style = MaterialTheme.typography.titleSmall
            )
            HorizontalDivider()
            SelectionContainer {
                Text(
                    text = clientDetails.clientAddress,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        GoogleMap(
            modifier = Modifier
                .sizeIn(maxWidth = 400.dp)
                .height(200.dp)
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = rememberUpdatedMarkerState(position = LatLng(clientDetails.latitude, clientDetails.longitude)),
                title = clientDetails.clientName
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .sizeIn(maxWidth = 400.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),

                onClick = onDeleteClicked,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, null)
                Text(text = stringResource(R.string.btn_delete))
            }

            Button(
                modifier = Modifier.weight(1f),

                onClick = onEditClicked
            ) {
                Icon(Icons.Filled.Edit, null)
                Text(text = stringResource(R.string.btn_edit))
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsComposable(
    clientDetails: ClientDetailsUiModel = ClientDetailsUiModel.dummy,
    onNavigateBack: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    val titles = listOf(stringResource(R.string.tab_details), stringResource(R.string.tab_purchases))
    val pagerState = rememberPagerState { titles.size }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.top_bar_client_details)) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        null
                    )
                }
            })

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(pagerState.currentPage),
                    width = 100.dp
                )
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> ClientDetailsInfoComposable(
                    clientDetails = clientDetails,
                    onDeleteClicked = onDeleteClicked,
                    onEditClicked = onEditClicked
                )

                1 -> ClientPurchasesComposable(purchases = clientDetails.purchases)
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

    var showDeleteDialog by remember { mutableStateOf(false) }

    ClientDetailsComposable(
        clientDetails = uiState,
        onNavigateBack = onBackToList,
        onDeleteClicked = { showDeleteDialog = true },
        onEditClicked = onEditClicked
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.isClientDeleted) {
            onBackToList()
        }
    }

    if (showDeleteDialog)
        ActionDialog(
            dialogType = DialogType.Delete,
            message =
                stringResource(R.string.delete_client_message),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmClicked = {
                showDeleteDialog = false
                clientDetailsViewModel.onDeleteClientAction(uiState.clientId)
            })
}
