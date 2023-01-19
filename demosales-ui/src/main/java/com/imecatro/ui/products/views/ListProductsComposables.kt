package com.imecatro.ui.products.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.ui.R
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.theme.PuntroSalesDemoTheme


@Composable
fun ListOfProducts(list: List<ProductUiModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { product ->
            ProductCardCompose(product = product)
        }
    }
}

@Composable
fun ProductCardCompose(product: ProductUiModel) {

    OutlinedCard(
        modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // TODO add description and implement image by url
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_hide_source_24),
                contentDescription = null
            )
            Column {
                Text(text = product.name ?: "Product name")
                Row {
                    Text(text = "$${product.price ?: 0.00}")
                    Text(text = " x ${product.unit}")
                }
            }
        }
    }
}

fun fakeProductsList(qty: Int): List<ProductUiModel> {
    val fakeList = mutableListOf<ProductUiModel>()
    for (i in 1..qty) {
        fakeList.add(ProductUiModel(i, "Product $i", "3.00", "pz", null))
    }
    return fakeList
}

@Preview(showBackground = true)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            ListOfProducts(list = fakeProductsList(20))
        }
    }
}