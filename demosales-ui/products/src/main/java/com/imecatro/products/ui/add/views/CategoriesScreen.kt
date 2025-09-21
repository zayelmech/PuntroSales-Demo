package com.imecatro.products.ui.add.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.ui.theme.dialogs.InputTextDialogComposable
import com.imecatro.products.ui.R

@Preview(showBackground = true)
@Composable
fun CategoriesScreen(
    categories: List<String> = emptyList(),
    onEditCategory: (Int) -> Unit = {},
    onDeleteCategory: (Int) -> Unit = {},
) {

    var showEditDialog: Boolean by remember { mutableStateOf(false) }

    LazyColumn {
        items(categories) { category ->
            ListItem(
                headlineContent = {
                    Text(category)
                },
                trailingContent = {
                    Row {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, "Edit category")
                        }

                        IconButton(onClick = { onDeleteCategory(categories.indexOf(category)) }) {
                            Icon(Icons.Default.Delete, "Delete category")
                        }

                    }
                }
            )
        }
    }

    if (showEditDialog)
        InputTextDialogComposable(
            supportingMessage = stringResource(R.string.edit_category, "this category"),
            onDismissRequest = { showEditDialog = false }
        ) {
            //addViewModel.onAddCategory(it)
            showEditDialog = false
        }

}