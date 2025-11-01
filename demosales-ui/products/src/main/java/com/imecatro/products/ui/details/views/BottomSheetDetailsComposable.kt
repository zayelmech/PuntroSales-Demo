package com.imecatro.products.ui.details.views

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import com.imecatro.products.ui.R
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsComposableStateImpl(
    productDetailsViewModel: ProductsDetailsViewModel,
    pageSelected : Int = 0,
    onNavigateBack: () -> Unit,
    onProductDeleted: () -> Unit,
    onNavigateToEdit: () -> Unit,
) {

    val productSelected by productDetailsViewModel.product.collectAsState()

    LaunchedEffect(productSelected.productDeleted) {
        if (productSelected.productDeleted)
            onProductDeleted()
    }

    val titles = listOf(stringResource(R.string.tab_details), stringResource(R.string.tab_stock))
    val pagerState = rememberPagerState(initialPage = pageSelected) { titles.size }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(text = stringResource(R.string.top_bar_product_details)) }, navigationIcon = {
            IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
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
                    R.string.delete_product_message),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmClicked = {
                showDeleteDialog = false
                productDetailsViewModel.onDeleteAction()
            })

}


@Composable
fun DetailsComposable(
    productDetails: ProductDetailsUiModel?,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    val view = LocalView.current

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .sizeIn(maxWidth = 450.dp, minWidth = 320.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter =
                    if (view.isInEditMode)
                        painterResource(id = R.drawable.baseline_insert_photo_24)
                    else rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(productDetails?.imageUrl)
                            .error(R.drawable.baseline_insert_photo_24)
                            .crossfade(true)
                            .build()
                    ),
                contentDescription = null,
                modifier = Modifier
                    .requiredSizeIn(maxHeight = 320.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                //tittle
                Text(
                    text = productDetails?.name ?: "No name",
                    style = MaterialTheme.typography.headlineMedium
                )
                //pz
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = productDetails?.unit ?: "",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(text = productDetails?.categoryName?:"",style = MaterialTheme.typography.labelMedium)
                }
                //price
                val locale: Locale = Locale.getDefault()
                val currency = Currency.getInstance(locale)
                Text(
                    text = "${productDetails?.price?.formatAsCurrency() ?: "0.00"} ${productDetails?.currency ?: currency.symbol}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row (Modifier.fillMaxWidth()){
                    Spacer(modifier = Modifier.weight(1f))
                        if (productDetails?.barcode?.isNotEmpty() == true){
                            Icon(painterResource(R.drawable.upc), "barcode")
                            Text(text = productDetails.barcode, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
                    }
                }

                //Details
                Text(text = stringResource(R.string.label_details), style = MaterialTheme.typography.labelMedium)

                Text(
                    text = productDetails?.details
                        ?: "This text must contains some details about this product",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(40.dp))

            }

        }
        Spacer(modifier = Modifier.height(30.dp))

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
                Icon( Icons.Filled.Delete, null)
                Text(text = stringResource(R.string.btn_delete))
            }

            Button(
                modifier = Modifier.weight(1f),

                onClick = onEditClicked) {
                Icon(Icons.Filled.Edit, null)
                Text(text = stringResource(R.string.btn_edit))
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
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