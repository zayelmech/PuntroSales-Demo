package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.theme.GreenTurquoise
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography

@Composable
fun CreateTicketComposable(
    productOnCart: List<ProductOnCartUiModel>,
    onDeleteProduct: (Int) -> Unit,
) {
    LazyColumn {
        itemsIndexed(productOnCart) { index, item ->
            OrderOnCartComposable(product = item, index, {}) {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderOnCartComposable(
    product: ProductOnCartUiModel,
    productPosition: Int,
    onPlusClicked: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,

    ) {

    val cardTag = "CARD-${product.product.id}"

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth()
//            .width(350.dp)
//        .clickable { onCardClicked() }
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

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(product.product.imageUri)
                        .error(R.drawable.baseline_insert_photo_24)
                        .crossfade(true)
                        .build()

                ),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.FillBounds
            )

            Column {
                Text(text = product.product.name ?: "Product name", style = Typography.titleMedium)
//                Text(text = " x ${product.product.unit}", fontSize = 18.sp)
                Text(text = "$${product.product.price ?: 0.00}", style = Typography.titleMedium)

            }
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .padding(5.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                OutlinedTextField(value = "${product.qty}", onValueChange = { })

                Row(Modifier.padding(5.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onMinusClick(productPosition) }
                            .background(GreenTurquoise, RoundedCornerShape(50)),
                        tint = Color.White

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Click to add 1 ",
                        modifier = Modifier
                            .clickable { onPlusClicked(productPosition) }
                            .background(GreenTurquoise, RoundedCornerShape(50)),
                        tint = Color.White
                    )


                }
            }
            //TODO show subtotal
            Text(text = "$${product.subtotal}", style = Typography.titleMedium)

        }
    }
}

fun createFakeListOfProductsOnCart(num: Int): List<ProductOnCartUiModel> {
    val list: MutableList<ProductOnCartUiModel> = mutableListOf()
    for (i in 1..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1f,
            imageUri = null
        )
        val new2 = ProductOnCartUiModel(new, 0f, 0f)

        list.add(new2)
    }
    return list
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateTicketComposable() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTicketComposable(createFakeListOfProductsOnCart(5)) {}

        }
    }
}

