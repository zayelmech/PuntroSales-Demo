package com.imecatro.products.ui.details.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.R
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsComposableStateImpl(
    productDetailsViewModel: ProductsDetailsViewModel,
    productId: Int?,
    onNavigateToEdit: (Int?) -> Unit
) {

    val productSelected by productDetailsViewModel.product.collectAsState()

    LaunchedEffect(Unit) {
        productDetailsViewModel.getDetailsById(productId)
    }

    var state by remember { mutableIntStateOf(0) }
    val titles = listOf(
        "Details",
        "Stock",
    )

    Column {
        PrimaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(selected = state == index,
                    onClick = { state = index },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    })
            }
        }
        if (state == 0)
            DetailsComposable(
                productDetails = productSelected,
                onDeleteClicked = {
                    onNavigateToEdit(null)
                    productDetailsViewModel.onDeleteAction(productId)
                },
                onEditClicked = {
                    productId?.let {
                        onNavigateToEdit(it)
                    }
                }
            )
        else
            StockComposable(
                stock = productSelected.stockQty,
                cost = productSelected.stockPrice,
                list = productSelected.stockHistory,
                onStockAdded = { productDetailsViewModel.onStockAdded(it) },
                onStockOut = { productDetailsViewModel.onStockRemoved(it) })
    }


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
            .padding(horizontal = 20.dp),
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
                    .requiredSizeIn(maxHeight = 280.dp)
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
                Text(
                    text = productDetails?.unit ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )
                //price
                Text(
                    text = "$${productDetails?.price ?: "0.00"} ${productDetails?.currency ?: ""}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(20.dp))

                //Details
                Text(text = "Details", style = MaterialTheme.typography.labelMedium)

                Text(
                    text = productDetails?.details
                        ?: "This text must contains some details about this product",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(40.dp))

            }

        }
        Spacer(modifier = Modifier.height(40.dp))
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
                    stockPrice = "0"
                ),
                onDeleteClicked = { /*TODO*/ }) {

            }
        }
    }
}