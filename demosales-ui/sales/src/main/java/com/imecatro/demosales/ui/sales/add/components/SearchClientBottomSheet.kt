package com.imecatro.demosales.ui.sales.add.components


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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.model.ClientResultUiModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme

@Composable
fun SearchClientBottomSheet(
    searchEngineUiModel: SearchClientEngineModel = SearchClientEngineModel()
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
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            placeholder = { Text(text = stringResource(R.string.search_client_hint)) },
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
            items(searchEngineUiModel.list) { client ->
                ClientResultCardComposable(
                    client = client,
                    onClientClicked = { searchEngineUiModel.onClientClicked(client) })
            }
            if (searchEngineUiModel.list.size < 4) {
                repeat(4) {
                    item { Box(modifier = Modifier.size(20.dp)) }
                }

            }
        }
    }
}

data class SearchClientEngineModel(
    val list: List<ClientResultUiModel> = listOf(),
    val query: String = "",
    val onQueryChange: (String) -> Unit = {},
    val onClientClicked: (ClientResultUiModel) -> Unit = {}
)

@Composable
fun ClientResultCardComposable(client: ClientResultUiModel, onClientClicked: (Long) -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClientClicked(client.id) }
            .padding(5.dp)
            .wrapContentSize(Alignment.TopEnd)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxSize(),
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(client.imageUri)
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
                Text(text = client.name, style = MaterialTheme.typography.labelMedium)
            }

        }
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(50)
            ),
            tint = Color.White
        )
    }
}

private fun createFakeClientList(num: Int): List<ClientResultUiModel> {
    val list: MutableList<ClientResultUiModel> = mutableListOf()
    for (i in 1..num) {
        val new = ClientResultUiModel(
            id = i.toLong(),
            name = "Product name $i",
            address = "",
            imageUri = null,
        )
        list.add(new)
    }
    return list
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBottomSheetClientComposable() {

    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val x = SearchClientEngineModel(createFakeClientList(4), "a", {}, {})
            SearchClientBottomSheet(x)
        }
    }
}

