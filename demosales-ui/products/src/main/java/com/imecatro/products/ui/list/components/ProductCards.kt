package com.imecatro.products.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.imecatro.products.ui.list.images.ProductThumbnails
import android.net.Uri
import kotlin.math.roundToInt
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.products.ui.R
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.views.ListProductsTestTags

@Preview
@Composable
fun ProductCardCompose(
    product: ProductUiModel = ProductUiModel("test"),
    isSelected: Boolean = product.isSelected,
    onLongClicked: () -> Unit = {},
    onCardClicked: () -> Unit = {}
) {

    val context = LocalContext.current
    val pixels = with(LocalDensity.current) { 80.dp.toPx().roundToInt() }
    val thumbnail by produceState<Uri?>(null, product.imageUrl, pixels) {
        value = null
        value = product.imageUrl?.let { ProductThumbnails.uri(context.applicationContext, it, pixels) }
    }
    val imageRequest = remember(context, thumbnail, pixels) {
        ImageRequest.Builder(context).data(thumbnail).size(pixels, pixels)
            .error(R.drawable.baseline_insert_photo_24)
            .placeholder(R.drawable.baseline_insert_photo_24)
            .fallback(R.drawable.baseline_insert_photo_24)
            .build()
    }

    val cardTag = "${ListProductsTestTags.CARD.tag}-${product.id}"

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else Color.Transparent


    Box(
        modifier = Modifier
            .background(backgroundColor)
            .combinedClickable(
                onClick = onCardClicked,
                onLongClick = onLongClicked
            )
            .testTag(cardTag),
    ) {
        ListItem(
            leadingContent = {

                Box(contentAlignment = Alignment.TopEnd) {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(25)),
                        contentScale = ContentScale.FillWidth
                    )
                    if (isSelected)
                        Icon(
                            Icons.Default.CheckCircle,
                            "Selected",
                            tint = MaterialTheme.colorScheme.tertiary
                        )

                }
            }, headlineContent = {
                Text(text = product.name ?: "Product name")
            }, supportingContent = {
                Text(text = "${product.price?.formatAsCurrency()}")
            }, trailingContent = {
                val color =
                    if (product.stock.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                Text(text = "${product.stock}  ${product.unit}", color = color)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }

}
