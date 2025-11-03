package com.imecatro.demosales.ui.sales.add.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.demosales.ui.theme.common.formatAsCurrency

@Composable
fun SearchBottomSheetComposable(
    searchEngineUiModel: SearchEngineUiModel = SearchEngineUiModel(),
    onDeductProductClicked: (ProductResultUiModel) -> Unit = {},
    onAddProductClicked: (ProductResultUiModel) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 300.dp)
            .padding(horizontal = 5.dp)
    ) {
        OutlinedTextField(
            value = searchEngineUiModel.query,
            onValueChange = searchEngineUiModel.onQueryChange,
            placeholder = { Text(text = "Search") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            })
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 5.dp)
        ) {
            items(searchEngineUiModel.list) { product ->
                ProductResultCardComposable(
                    product = product,
                    onDeductProductClicked = { onDeductProductClicked(product) },
                    onAddProductClicked = { onAddProductClicked(product) })
            }
            if (searchEngineUiModel.list.size < 4) {
                repeat(4) {
                    item { Box(modifier = Modifier.size(20.dp)) }
                }

            }
        }
    }
}

data class SearchEngineUiModel(
    val list: List<ProductResultUiModel> = listOf(),
    val query: String = "",
    val onQueryChange: (String) -> Unit = {}
)

@Composable
fun ProductResultCardComposable(
    product: ProductResultUiModel,
    onDeductProductClicked: (Long) -> Unit = {},
    onAddProductClicked: (Long) -> Unit = {}
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(product.imageUri)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (product.qty == 0.0) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onAddProductClicked(product.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(50)
                            ),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                } else
                    PlusDeductItem(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(
                                alpha = 0.5f
                            ), shape = RoundedCornerShape(5.dp)
                        ),
                        value = "${product.qty}",
                        readOnly = true,
                        onPlusClicked = { onAddProductClicked(product.id) },
                        onMinusClick = { onDeductProductClicked(product.id) })
            }
        }
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 5.dp)) {
            val name = product.name
            Text(text = name, fontSize = 16.sp, style = Typography.titleMedium)

            val price = product.price.formatAsCurrency()
            Text(text = price, style = MaterialTheme.typography.labelMedium)

            val stockWarning =
                if (product.stock <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            Text(
                text = "Stock: ${product.stock}",
                color = stockWarning,
                style = MaterialTheme.typography.bodyMedium
            )

        }

    }
}

fun createFakeList(num: Int): List<ProductResultUiModel> {
    val list: MutableList<ProductResultUiModel> = mutableListOf()
    for (i in 1L..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1.0,
            imageUri = null
        )
        list.add(new)
    }
    return list
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBottomSheetComposable() {

    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val x = SearchEngineUiModel(createFakeList(4), "a", {})
            SearchBottomSheetComposable(x)
        }
    }
}

