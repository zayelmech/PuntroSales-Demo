package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun OrderOnCartComposable(
    product: ProductOnCartUiModel,
    productPosition: Int,
    onPlusClicked: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,
    onQtyValueChange: (String) -> Unit,
) {

    val cardTag = "CARD-${product.product.id}"
    var showDialog by remember {
        mutableStateOf(false)
    }

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth()
//            .height(120.dp)
//        .clickable { onCardClicked() }
            .testTag(cardTag),
        elevation = CardDefaults.cardElevation(0.5.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
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
//                    .padding(5.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.FillBounds
            )

            Column(Modifier.weight(1f)) {
                Text(text = product.product.name ?: "Product name", style = Typography.titleMedium)
//                Text(text = " x ${product.product.unit}", fontSize = 18.sp)
                Text(text = "$${product.product.price ?: 0.00}", style = Typography.titleMedium)

            }
            Column(
                modifier = Modifier
//                    .width(90.dp)
                    .padding(5.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


//                OutlinedTextField(
//                    value = "${product.qty}",
//                    onValueChange = onQtyValueChange,
//                    modifier = Modifier.padding(0.dp),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//                )

//                TextButton(onClick = { showDialog = true }) {
//                    Text(text = "${product.qty}")
//                }

                if (showDialog) {
                    InputNumberDialogComposable(
                        initialValue = "${product.qty}",
                        onDismissRequest = { showDialog = false },
                        onConfirmClicked = { onQtyValueChange(it);showDialog = false }
                    )
                }
                Row(Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onMinusClick(productPosition) }
                            .background(GreenTurquoise, RoundedCornerShape(50)),
                        tint = Color.White

                    )
                    TextButton(onClick = { showDialog = true }) {
                        Text(text = "x ${product.qty}")
                    }
//                    Spacer(modifier = Modifier.width(5.dp))
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
            Text(text = "$${product.subtotal}", style = Typography.titleMedium)

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewOrderOnCartComposable() {
    PuntroSalesDemoTheme {
        Surface {
            OrderOnCartComposable(
                product = ProductOnCartUiModel(
                    product = ProductResultUiModel(id=1, name = "product", price = 3f, imageUri = null),
                    qty = 1f,
                    subtotal = 23f.toBigDecimal()
                ),
                productPosition = 0,
                onPlusClicked = {},
                onMinusClick = {},
                onQtyValueChange = {},
            )
        }
    }
}
