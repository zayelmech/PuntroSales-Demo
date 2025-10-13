package com.imecatro.demosales.navigation.products

import android.os.Parcelable
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.imecatro.demosales.navigation.products.MyProduct.Companion.MODE_CATALOG
import com.imecatro.demosales.navigation.products.MyProduct.Companion.MODE_CATEGORIES
import com.imecatro.products.ui.catalog.screens.CatalogPreview
import com.imecatro.products.ui.categories.screens.CategoriesScreenImpl
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAndDetailsPane(
    onAddProduct: () -> Unit = {},
    onEditProduct: (Long) -> Unit = {}
) {

    val navigator = rememberSupportingPaneScaffoldNavigator<MyProduct>()
    val scope = rememberCoroutineScope()
    val viewModel: ProductsDetailsViewModel =
        hiltViewModel(creationCallback = { f: ProductsDetailsViewModel.Factory -> f.create(0L) })


    val productsViewModel: ProductsViewModel = hiltViewModel()


    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane {
                ListOfProductsStateImpl(
                    productsViewModel = productsViewModel,
                    onCreateCatalog = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = SupportingPaneScaffoldRole.Extra,
                                MyProduct(0, MODE_CATALOG)
                            )
                        }
                    },
                    onCategoriesNav = {
                        scope.launch {
                            navigator.navigateTo(
                                pane = SupportingPaneScaffoldRole.Extra,
                                MyProduct(0, MODE_CATEGORIES)
                            )
                        }
                    }) { id ->
                    if (id != null) {
                        scope.launch {
                            viewModel.loadDetailsForProduct(id)
                            navigator.navigateTo(
                                pane = SupportingPaneScaffoldRole.Supporting,
                                MyProduct(id)
                            )
                        }
                    } else onAddProduct()
                }
            }
        }, supportingPane = {

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
            val c = navigator.currentDestination?.contentKey?.mode
            AnimatedPane {
                if (c == MODE_CATEGORIES)
                    CategoriesScreenImpl(categoriesViewModel = hiltViewModel(), onNavigateBack = {
                        scope.launch { navigator.navigateBack() }
                    })
                else
                    CatalogPreview(productsViewModel = productsViewModel, onBack = {
                        scope.launch { navigator.navigateBack() }
                    })
            }
        }
    )
}

@Parcelize
class MyProduct(val id: Long, val mode: Int = 0) : Parcelable {

    companion object {
        const val MODE_CATEGORIES = 1
        const val MODE_CATALOG = 2
    }
}