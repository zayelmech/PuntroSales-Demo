package com.imecatro.ui.products.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.imecatro.ui.R
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.theme.PuntroSalesDemoTheme
import com.imecatro.ui.theme.Typography


@Composable
fun ListOfProducts(list: List<ProductUiModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { product ->
            ProductCardCompose(product = product, {})
        }
    }
}

val imgTest =
    "https://imecatro.com/img/mechanic.jpg"

//@OptIn(ExperimentalGlideComposeApi::class)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCardCompose(product: ProductUiModel, onCardClicked: () -> Unit) {

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 5.dp)
            .width(350.dp)
            .clickable { onCardClicked },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // TODO add description and implement image by url
            product.imageUrl?.let { link ->
                GlideImage(
                    model = link,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(100.dp)
                        .clip(RoundedCornerShape(25))
                        .border(
                            BorderStroke(1.dp, Color.Transparent),
                            shape = RoundedCornerShape(25)
                        )//.clickable(onClick = onClick).fillParentMaxSize(),
                )

//                AsyncImage(
//                    model = link,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(60.dp)
//                        .padding(5.dp)
//                )
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_baseline_hide_source_24),
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