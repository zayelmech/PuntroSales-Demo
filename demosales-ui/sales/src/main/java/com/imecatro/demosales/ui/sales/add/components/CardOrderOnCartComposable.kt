package com.imecatro.demosales.ui.sales.add.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import java.math.BigDecimal


private val dummyProduct =
    ProductOnCartUiModel(
        orderId = 0,
        product = ProductResultUiModel(name = "Test"),
        qty = 0.0,
        subtotal = BigDecimal(0)
    )

@Preview(showBackground = true)
@Composable
fun OrderOnCartComposable(
    product: ProductOnCartUiModel = dummyProduct,
    productPosition: Int = 0,
    onPlusClicked: (Int) -> Unit = {},
    onMinusClick: (Int) -> Unit = {},
    onQtyValueChange: (String) -> Unit = {},
) {
    val context = LocalContext.current

    val cardTag = "CARD-${product.product.id}"
    Box(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth()
            .testTag(cardTag),
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            leadingContent = {
                // TODO add description and implement image by url
                Log.d("TAG", "OrderOnCartComposable: ${product.product.imageUri}")
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(product.product.imageUri)
                            .error(R.drawable.baseline_insert_photo_24)
                            .crossfade(false)
                            .build()

                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp),
                        //.clip(RoundedCornerShape(50)),
                    contentScale = ContentScale.FillWidth
                )
            },
            headlineContent = {
                Text(text = product.product.name)
            },
            supportingContent = {
                Text(text = product.subtotal.toDouble().formatAsCurrency())
            }, trailingContent = {
                PlusDeductItem(
                    value = "${product.qty}",
                    onValueChange = { onQtyValueChange(it) },
                    onPlusClicked = { onPlusClicked(productPosition) },
                    onMinusClick = { onMinusClick(productPosition) })
            }
        )
    }
}


@Composable
fun PlusDeductItem(
    modifier: Modifier = Modifier,
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onPlusClicked: () -> Unit = {},
    onMinusClick: () -> Unit = {},
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onMinusClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_remove_24),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(50)
                    ),
                contentDescription = "Click to minus 1",
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                ),
            singleLine = true,
            readOnly = readOnly,
            enabled = !readOnly,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.sizeIn(minWidth = 40.dp, maxWidth = 50.dp, minHeight = 24.dp)
        )
        IconButton(onClick = onPlusClicked) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Click to add 1",
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(50)
                    ),
            )
        }
    }
}