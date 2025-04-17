package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun SearchBottomSheetComposable(
    searchEngineUiModel: SearchEngineUiModel = SearchEngineUiModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 300.dp)
            .padding(5.dp)
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
        ) {
            items(searchEngineUiModel.list) { product ->
                ProductResultCardComposable(
                    product = product,
                    onProductClicked = { searchEngineUiModel.onProductClicked(product) })
            }
            if (searchEngineUiModel.list.size < 4) {
                repeat(4){
                    item {  Box(modifier = Modifier.size(20.dp))}
                }

            }
        }
    }
}

data class SearchEngineUiModel(
    val list: List<ProductResultUiModel> = listOf(),
    val query: String = "",
    val onQueryChange: (String) -> Unit = {},
    val onProductClicked: (ProductResultUiModel) -> Unit = {}
)

@Composable
fun ProductResultCardComposable(product: ProductResultUiModel, onProductClicked: (Int) -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onProductClicked(product.id ?: 0) }
        .padding(5.dp)
        .wrapContentSize(Alignment.TopEnd)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxSize(),
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(product.imageUri)
                        .error(R.drawable.baseline_insert_photo_24)
                        .crossfade(false)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.FillWidth
            )
            Column(modifier = Modifier.padding(5.dp)) {
                val name = product.name ?: "Unknown"
                Text(text = name, fontSize = 16.sp, style = Typography.titleMedium)

                val price = "$ ${product.price ?: 0}"
                Text(text = price, fontSize = 18.sp, style = Typography.titleMedium)
            }

        }
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
            tint = Color.White
        )
    }
}

fun createFakeList(num: Int): List<ProductResultUiModel> {
    val list: MutableList<ProductResultUiModel> = mutableListOf()
    for (i in 1..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1f,
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
            val x = SearchEngineUiModel(createFakeList(4), "a", {}, {})
            SearchBottomSheetComposable(x)
        }
    }
}

