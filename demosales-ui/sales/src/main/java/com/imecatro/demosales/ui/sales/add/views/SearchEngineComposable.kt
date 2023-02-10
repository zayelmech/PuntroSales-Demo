package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.theme.BlueTurquoise80
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheetComposable(
    list: List<ProductResultUiModel>,
    query: String,
    onQueryChange: (String) -> Unit,
    onProductClicked: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            })
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp), modifier = Modifier.height(300.dp)) {
            items(list) {
                ProductResultCardComposable(product = it, onProductClicked = onProductClicked)
            }
        }
    }
}


@Composable
fun ProductResultCardComposable(product: ProductResultUiModel, onProductClicked: (Int) -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onProductClicked(product.id ?: 0) }
        .padding(5.dp)
//        .wrapContentSize(Alignment.TopEnd)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(Color.White)
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
//                    .size(128.dp,100.dp)
//                    .clip(RoundedCornerShape(25)),
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
            modifier = Modifier.background(BlueTurquoise80, RoundedCornerShape(50)),
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
            SearchBottomSheetComposable(createFakeList(20), "a", {}, {})
        }
    }
}

