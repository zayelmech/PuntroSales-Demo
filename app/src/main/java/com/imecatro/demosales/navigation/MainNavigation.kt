package com.imecatro.demosales.navigation

import android.os.Parcelable
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.imecatro.products.ui.categories.screens.CategoriesScreenImpl
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAndDetailsPane(
    onAddProduct: () -> Unit = {},
    onEditProduct: (Long) -> Unit = {}
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<MyItem>()
    val scope = rememberCoroutineScope()
    val viewModel: ProductsDetailsViewModel =
        hiltViewModel(creationCallback = { f: ProductsDetailsViewModel.Factory -> f.create(0L) })


    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            ListOfProductsStateImpl(productsViewModel = hiltViewModel(), onCategoriesNav = {
                scope.launch {
                    navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra, MyItem(0))

                }
            }) { id ->
                if (id != null) {
                    scope.launch {
                        viewModel.loadDetailsForProduct(id)
                        navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail, MyItem(id))
                    }
                } else onAddProduct()
            }
        }, detailPane = {

            AnimatedPane {
                navigator.currentDestination?.contentKey?.let { navArgs ->
                    DetailsComposableStateImpl(
                        productDetailsViewModel = viewModel,
                        onNavigateBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        onProductDeleted = {
                            scope.launch { navigator.navigateBack() }
                        },
                        onNavigateToEdit = { onEditProduct(navArgs.id) }
                    )
                }
            }
        },
        extraPane = {
            AnimatedPane {
                CategoriesScreenImpl(categoriesViewModel = hiltViewModel(), onNavigateBack = {
                    scope.launch { navigator.navigateBack() }
                })
            }
        }
    )
}

@Parcelize
class MyItem(val id: Long) : Parcelable