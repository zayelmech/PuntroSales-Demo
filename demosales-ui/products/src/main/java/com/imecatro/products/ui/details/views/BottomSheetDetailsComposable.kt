package com.imecatro.products.ui.details.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.common.download
import com.imecatro.demosales.ui.theme.common.open
import com.imecatro.demosales.ui.theme.common.share
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import com.imecatro.products.ui.R
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsComposableStateImpl(
    productDetailsViewModel: ProductsDetailsViewModel,
    pageSelected: Int = 0,
    onNavigateBack: () -> Unit,
    onProductDeleted: () -> Unit,
    onNavigateToEdit: () -> Unit,
) {

    val productSelected by productDetailsViewModel.uiState.collectAsState()

    LaunchedEffect(productSelected.productDeleted) {
        if (productSelected.productDeleted)
            onProductDeleted()
    }

    val titles = listOf(stringResource(R.string.tab_details), stringResource(R.string.tab_stock))
    val pagerState = rememberPagerState(initialPage = pageSelected) { titles.size }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.top_bar_product_details)) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
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
                .weight(1f) // take remaining space
        ) { page ->
            when (page) {
                0 -> DetailsComposable(
                    productDetails = productSelected,
                    onDeleteClicked = { showDeleteDialog = true },
                    onEditClicked = onNavigateToEdit
                )

                1 -> StockComposable(
                    stock = productSelected.stockQty,
                    cost = productSelected.stockPrice,
                    list = productSelected.stockHistory,
                    onDownloadClicked = { productDetailsViewModel.onStockHistoryDownload() },
                    onStockAdded = { productDetailsViewModel.onStockAdded(it) },
                    onStockOut = { productDetailsViewModel.onStockRemoved(it) }
                )
            }
        }
    }

    if (showDeleteDialog)
        ActionDialog(
            dialogType = DialogType.Delete,
            message =
                stringResource(
                    R.string.delete_product_message
                ),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmClicked = {
                showDeleteDialog = false
                productDetailsViewModel.onDeleteAction()
            })

    var showShareStockHistory by remember { mutableStateOf(false) }

    LaunchedEffect(productSelected) {
        if (productSelected.file != null)
            showShareStockHistory = true
    }

    val context = LocalContext.current
    if (showShareStockHistory)
        ModalBottomSheet(onDismissRequest = {
            showShareStockHistory = false
            //productsViewModel.onCatalogShared()
        }
        ) {
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        productSelected.file?.open(context)
                    }) {
                        Text(stringResource(R.string.btn_download_stokc_csv))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        productSelected.file?.share(context)
                    }) { Icon(Icons.Default.Share, null) }
                    IconButton(onClick = {
                        productSelected.file?.download(context)
                    }) { Icon(painterResource(R.drawable.download), null) }
                }
            }
        }
}



@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewWordsListDetailsCompose() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DetailsComposable(
                productDetails = ProductDetailsUiModel(
                    1,
                    "Product name",
                    "$0.00",
                    "x pz",
                    "USD",
                    null,
                    "details",
                    stockQty = "0.0",
                    stockHistory = emptyList(),
                    categoryName = "ca",
                    stockPrice = "0",
                    barcode = "ASD123"
                ),
                onDeleteClicked = { /*TODO*/ }) {

            }
        }
    }
}