package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheetComposable(
    list: List<ProductResultUiModel>,
    query: String,
    onQueryChange: (String) -> Unit,


    ) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {},
        active = true,
        onActiveChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(list) {
                Text(text = it.name.toString())
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBottomSheetComposable() {

    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchBottomSheetComposable(listOf(), "a", {})
        }
    }
}

