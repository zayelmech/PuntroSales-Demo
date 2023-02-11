package com.imecatro.products.ui.list.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.BlueTurquoise80
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.products.ui.R
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.uistate.ListProductsUiState
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import kotlinx.coroutines.launch


private const val TAG = "ListProductsComposables"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfProducts(
    list: List<ProductUiModel>,
    onCardClicked: (Int?) -> Unit,
    onNavigateAction: () -> Unit
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { onNavigateAction() },
            containerColor = BlueTurquoise80,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { innerPadding ->

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
}

@Composable
fun ProductCardCompose(product: ProductUiModel, onCardClicked: () -> Unit) {

    val cardTag = "${ListProductsTestTags.CARD.tag}-${product.id}"

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
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .error(R.drawable.baseline_insert_photo_24)
                        .crossfade(true)
                        .build()

                ),
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

fun fakeProductsList(qty: Int): List<ProductUiModel> {
    val fakeList = mutableListOf<ProductUiModel>()
    for (i in 1..qty) {
        fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", null))
    }
    return fakeList
}

/**
 * List of products state impl
 * This composable function is in charge of handling the UI events
 * observing states from the viewModel and remembering Coroutine Scope
 * in oder to make more lifecycle aware
 * @param productsViewModel this ViewModel of type ProductsViewModel is used hold the data and update the list once it's retrieve from the repository
 * @param onNavigateAction this lambda function allows you to navigate to another UI according to the value sent.<br> Example: onNavigate(1) will launch the Details screen of the product with the id = 1
 *
 */
@Composable
fun ListOfProductsStateImpl(
    productsViewModel: ProductsViewModel,
    onNavigateAction: (Int?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val productsList by productsViewModel.productsList.collectAsState()
    val uiState by productsViewModel.uiState.collectAsState()

    when (uiState) {
        is ListProductsUiState.Initialized -> {
            productsViewModel.getAllProducts()
        }
        is ListProductsUiState.Loading -> {/*TODO */
        }
        is ListProductsUiState.Success -> {/*TODO */
        }
        is ListProductsUiState.Error -> {/*TODO */
        }
    }


    ListOfProducts(
        list = productsList.toMutableStateList(),
        onCardClicked = {
            scope.launch { onNavigateAction(it) }
        },
        onNavigateAction = { onNavigateAction(null) })

}


@Preview(showBackground = true)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {

            ListOfProducts(list = fakeProductsList(20), {}) {
                Log.d(TAG, "PreviewListOfProducts: ")
            }
        }
    }
}

enum class ListProductsTestTags(val tag: String) {
    CARD("ElevatedCard")
}