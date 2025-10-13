package com.imecatro.products.ui.catalog.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.products.ui.R
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.views.fakeProductsList

/**
 *
 *
 */
@Preview(showBackground = true)
@Composable
fun ProductsCatalog(
    products: Map<String?, List<ProductUiModel>> = mapOf(
        "A" to fakeProductsList(5),
        "B" to fakeProductsList(11)
    ),
    columns: Int = 3
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        products.forEach { categoryKey, categoryProducts ->

            item(span = { GridItemSpan(maxLineSpan) }) {
                categoryProducts.first().category?.let { Text(it) }
                    ?: Text(stringResource(R.string.txt_uncategorized))
            }
            items(categoryProducts) {
                ProductCard(product = it)
            }
        }
    }
}

@Preview
@Composable
fun ProductCard(
    product: ProductUiModel = ProductUiModel("Apple")
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier
            .wrapContentSize()
            .sizeIn(maxWidth = 150.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(product.imageUrl)
                    .error(R.drawable.baseline_insert_photo_24)
                    .placeholder(R.drawable.baseline_insert_photo_24)
                    .crossfade(false)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.FillWidth
        )
        Column(modifier = Modifier.padding(horizontal = 3.dp, vertical = 5.dp)) {

            val name = "${product.name?:""} | ${product.unit?:""}"
            Text(text = name, fontSize = 16.sp, style = MaterialTheme.typography.titleSmall)

            val price = product.price?.formatAsCurrency()
            Text(text = price ?: "", style = MaterialTheme.typography.titleMedium)
        }
    }
}