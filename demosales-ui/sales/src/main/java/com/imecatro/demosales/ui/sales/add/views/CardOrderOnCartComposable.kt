package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.imecatro.demosales.ui.theme.dialogs.InputNumberDialogComposable
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

    val cardTag = "CARD-${product.product.id}"
    var showDialog by remember {
        mutableStateOf(false)
    }

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
                        .size(40.dp)
                        .clip(RoundedCornerShape(50)),
                    contentScale = ContentScale.FillBounds
                )
            },
            headlineContent = {
                Text(text = product.product.name)
            },
            supportingContent = {
                Text(text = "$${product.subtotal}", style = MaterialTheme.typography.bodyLarge)
                // Text(text = "$${product.product.price ?: 0.00}")
            }, trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onMinusClick(productPosition) }
                            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50)),
                        tint = Color.White

                    )
                    TextButton(onClick = { showDialog = true }) {
                        Text(text = "x ${product.qty}")
                    }
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Click to add 1 ",
                        modifier = Modifier
                            .clickable { onPlusClicked(productPosition) }
                            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50)),
                        tint = Color.White
                    )

                }
            }
        )

        if (showDialog) {
            InputNumberDialogComposable(
                initialValue = "${product.qty}",
                onDismissRequest = { showDialog = false },
                onConfirmClicked = { newQty ->
                    onQtyValueChange(newQty)
                    showDialog = false
                }
            )
        }

    }
}
