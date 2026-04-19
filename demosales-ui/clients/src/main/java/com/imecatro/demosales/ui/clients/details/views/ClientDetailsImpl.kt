package com.imecatro.demosales.ui.clients.details.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import com.imecatro.demosales.ui.clients.details.viewmodel.ClientDetailsViewModel
import com.imecatro.demosales.ui.theme.architect.isLoading
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsComposable(
    clientDetails: ClientDetailsUiModel = ClientDetailsUiModel.dummy,
    purchases: List<PurchaseUiModel> = emptyList(),
    isLoading: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {}
) {
    val titles =
        listOf(stringResource(R.string.tab_details), stringResource(R.string.tab_purchases))
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
            },
            actions = {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (clientDetails.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (clientDetails.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        if (isLoading)
            LinearProgressIndicator()

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
                0 -> ClientDetailsInfoScreen(
                    clientDetails = clientDetails,
                    onDeleteClicked = onDeleteClicked,
                    onEditClicked = onEditClicked
                )

                1 -> ClientPurchasesScreen(
                    accumulated = clientDetails.accumulatedPurchases,
                    purchases = purchases
                )
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

    val uiState by clientDetailsViewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }

    ClientDetailsComposable(
        clientDetails = uiState.clientDetails,
        purchases = uiState.purchases,
        isLoading = uiState.isLoading,
        onNavigateBack = onBackToList,
        onDeleteClicked = { showDeleteDialog = true },
        onEditClicked = onEditClicked,
        onFavoriteToggle = { clientDetailsViewModel.onToggleFavorite() }
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
                clientDetailsViewModel.onDeleteClient()
            })
}
