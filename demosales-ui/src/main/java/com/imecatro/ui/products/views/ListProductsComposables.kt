package com.imecatro.ui.products.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.imecatro.ui.R
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.products.viewmodels.ProductsViewModel
import com.imecatro.ui.theme.PuntroSalesDemoTheme
import com.imecatro.ui.theme.Typography


@Composable
fun ListOfProducts(list: List<ProductUiModel>, onCardClicked: (Int?) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { product ->
            ProductCardCompose(product = product) { onCardClicked(product.id) }
        }
    }
}

private const val TAG = "ListProductsComposables"
const val imgTest =
    "https://imecatro.com/img/mechanic.jpg"

@Composable
fun ProductCardCompose(product: ProductUiModel, onCardClicked: () -> Unit) {

    val cardTag = "${ListProductsTestTags.CARD.name}-${product.id}"

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth(0.9f)
//            .width(350.dp)
            .clickable { onCardClicked() }
            .testTag(cardTag),
        elevation = CardDefaults.cardElevation(0.5.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // TODO add description and implement image by url
            product.imageUrl?.let { link ->
                Image(
                    painter = rememberAsyncImagePainter(R.raw.arcreactor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(25)),
                    contentScale = ContentScale.FillWidth

                    //                        .border(
//                            BorderStroke(1.dp, Color.Transparent),
//                            shape = RoundedCornerShape(25)
//                        )//.clickable(onClick = onClick).fillParentMaxSize(),

                )
            } ?: Image(
                painter = rememberAsyncImagePainter(R.raw.arcreactor),
                contentDescription = null, modifier = Modifier
                    .size(60.dp)
                    .padding(5.dp), alignment = Alignment.Center
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
        fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", imgTest))
    }
    return fakeList
}

@Composable
fun ListOfProductsStateImpl(viewModel: ProductsViewModel) {

}


@Preview(showBackground = true)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            ListOfProducts(list = fakeProductsList(20)) {
                Log.d(TAG, "PreviewListOfProducts: $it")
            }
        }
    }
}

enum class ListProductsTestTags(value: String) {
    CARD("ElevatedCard")
}