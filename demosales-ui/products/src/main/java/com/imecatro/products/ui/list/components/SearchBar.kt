package com.imecatro.products.ui.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.products.ui.R


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchProductTopBar(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchAction: () -> Unit = {},
    showFilters: Boolean = false,
    onShowFiltersClicked: () -> Unit = {},
    onClearSearchBar: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.extraLarge,
            value = query,
            onValueChange = { onQueryChange(it) },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = {
                //if active onClearSearchBar()
                if (query.isEmpty())
                    Icon(Icons.Default.Search, null)
                else
                    IconButton(onClick = onClearSearchBar) {
                        Icon(Icons.Default.Clear, null)
                    }

            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions { onSearchAction() },
            trailingIcon = {

                IconButton(
                    onClick = onShowFiltersClicked,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = if (showFilters) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
                ) {
                    Icon(painterResource(R.drawable.sliders), null)
                }
            })
    }
}