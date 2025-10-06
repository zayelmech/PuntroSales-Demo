package com.imecatro.products.ui.categories.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import com.imecatro.demosales.ui.theme.dialogs.InputTextDialogComposable
import com.imecatro.products.ui.R
import com.imecatro.products.ui.categories.model.CategoryUiModel
import com.imecatro.products.ui.categories.viewmodel.CategoriesViewModel


private val fakeCategories =
    listOf(CategoryUiModel(1, "Category 1"), CategoryUiModel(2, "Category 2"))

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CategoriesScreen(
    categories: List<CategoryUiModel> = fakeCategories,
    backAction: () -> Unit = {},
    onEditCategory: (CategoryUiModel) -> Unit = {},
    onDeleteCategory: (Long) -> Unit = {},
    onAddCategory: () -> Unit = {}
) {

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        TopAppBar(
            title = { Text(stringResource(R.string.top_bar_title_categories)) }, actions = {
            IconButton(onClick = { onAddCategory() }) {
                Icon(Icons.Default.Add, "Add category")
            }
        },
            navigationIcon = {
                IconButton(onClick = { backAction() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Add category")
                }
            })
        LazyColumn {
            items(categories) { category ->
                ListItem(
                    headlineContent = {
                        Text(category.name)
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onEditCategory(category) }) {
                                Icon(Icons.Default.Edit, "Edit category")
                            }

                            IconButton(onClick = { onDeleteCategory(category.id) }) {
                                Icon(Icons.Default.Delete, "Delete category")
                            }

                        }
                    }
                )
            }
        }
    }


}


@Composable
fun CategoriesScreenImpl(
    categoriesViewModel: CategoriesViewModel,
    onNavigateBack: () -> Unit = {}
) {

    val uiState by categoriesViewModel.uiState.collectAsState()

    var showEditDialog: Boolean by remember { mutableStateOf(false) }

    var showAddDialog: Boolean by remember { mutableStateOf(false) }

    var deleteCategoryId: Long  by remember { mutableStateOf(0L) }

    var categoryEditable: CategoryUiModel by remember { mutableStateOf(CategoryUiModel(0, "")) }

    CategoriesScreen(
        categories = uiState.categories,
        backAction = { onNavigateBack() },
        onEditCategory = { categoryEditable = it },
        onDeleteCategory = { deleteCategoryId = it },
        onAddCategory = { showAddDialog = true }
    )

    LaunchedEffect(categoryEditable) {
        if (categoryEditable.id != 0L) {
            showEditDialog = true
        }
    }

    if (showEditDialog)
        InputTextDialogComposable(
            initialValue = categoryEditable.name,
            supportingMessage = stringResource(R.string.edit_category),
            onDismissRequest = { showEditDialog = false }
        ) { txt ->
            categoriesViewModel.onEditCategory(categoryEditable.copy(name = txt))
            categoryEditable = categoryEditable.copy(name = "", id = 0)
            showEditDialog = false
        }


    if (showAddDialog)
        InputTextDialogComposable(
            supportingMessage = stringResource(R.string.add_new_category),
            onDismissRequest = { showAddDialog = false }
        ) { txt ->
            categoriesViewModel.onAddCategory(txt)
            showAddDialog = false
        }


    if (deleteCategoryId > 0L)
        ActionDialog(
            dialogType = DialogType.Delete,
            message =
                stringResource(R.string.delete_category_message),
            onDismissRequest = {
                deleteCategoryId = 0L
            },
            onConfirmClicked = {
                deleteCategoryId = 0L
                categoriesViewModel.deleteCategory(deleteCategoryId)
            })
}