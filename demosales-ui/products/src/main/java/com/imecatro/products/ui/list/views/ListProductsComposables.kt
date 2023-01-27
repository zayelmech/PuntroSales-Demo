package com.imecatro.products.ui.list.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.imecatro.products.ui.R
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import com.imecatro.products.ui.theme.BlueTurquoise80
import com.imecatro.products.ui.theme.GreenTurquoise
import com.imecatro.products.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.theme.Typography
import kotlinx.coroutines.launch


private const val TAG = "ListProductsComposables"

@Composable
fun ListOfProducts(
    list: List<ProductUiModel>,
    innerPadding: PaddingValues,
    onCardClicked: (Int?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = innerPadding
    ) {
        items(list) { product ->
            ProductCardCompose(product = product) { onCardClicked(product.id) }
        }
    }
}

@Composable
fun ProductCardCompose(product: ProductUiModel, onCardClicked: () -> Unit) {

    val cardTag = "${ListProductsTestTags.CARD.name}-${product.id}"

    ElevatedCard(modifier = Modifier
        .padding(2.dp, 2.dp)
        .fillMaxWidth(0.9f)
//            .width(350.dp)
        .clickable { onCardClicked() }
        .testTag(cardTag),
        elevation = CardDefaults.cardElevation(0.5.dp),
        colors = CardDefaults.cardColors(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // TODO add description and implement image by url

            Image(
                painter = rememberAsyncImagePainter(product.imageUrl ?: R.raw.arcreactor),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(25)),
                contentScale = ContentScale.FillWidth
            )

            Column {
                Text(text = product.name ?: "Product name", style = Typography.titleMedium)
                Text(text = " x ${product.unit}", fontSize = 18.sp)
                Text(text = "$${product.price ?: 0.00}", style = Typography.titleMedium)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfProductsPlusFloatIcon(
    list: List<ProductUiModel>,
    onCardClicked: (Int?) -> Unit,
    onNavigateAction: () -> Unit
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateAction() }, containerColor = BlueTurquoise80, contentColor = Color.White) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { innerPadding ->
        ListOfProducts(list, innerPadding, onCardClicked)
    }
}

fun fakeProductsList(qty: Int): List<ProductUiModel> {
    val fakeList = mutableListOf<ProductUiModel>()
    for (i in 1..qty) {
        fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", "null"))
    }
    return fakeList
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListOfProductsStateImpl(
    productsViewModel: ProductsViewModel,
    onNavigateAction: (Int?) -> Unit,
    //TODO ON NAVIGATE NEW
) {
    val scope = rememberCoroutineScope()
    //val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val _list by productsViewModel.productsList.collectAsState()
    productsViewModel.getAllProducts()

    ListOfProductsPlusFloatIcon(_list.toMutableStateList(),
        onCardClicked = {
            scope.launch {

                onNavigateAction(it )
//                    productSelected = productDetailsUiModel.getDetailsById(it)?: initDetails
//                    state.show()
            }
        }) {
        onNavigateAction(null)
    }
//    BottomSheetDetailsCompose(
//        productDetails = productSelected,
//        state = state,
//        onDeleteClicked = {
//            scope.launch {
//                productDetailsUiModel.onDeleteAction(productSelected.id)
//                state.hide()
//            }
//        },
//        onEditClicked = { onNavigateAction(productSelected.id) },
//
//        ) {
//        ListOfProductsPlusFloatIcon(_list.toMutableStateList(),
//            onCardClicked = {
//                scope.launch {
//                    productSelected = productDetailsUiModel.getDetailsById(it)?: initDetails
//                    state.show()
//                }
//            }) {
//            onNavigateAction(null)
//        }
//    }
}


@Preview(showBackground = true)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {

            ListOfProductsPlusFloatIcon(list = fakeProductsList(20), {}) {
                Log.d(TAG, "PreviewListOfProducts: ")
            }
        }
    }
}

enum class ListProductsTestTags(value: String) {
    CARD("ElevatedCard")
}