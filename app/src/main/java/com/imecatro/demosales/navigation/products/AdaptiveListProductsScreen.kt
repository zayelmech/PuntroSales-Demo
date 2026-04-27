package com.imecatro.demosales.navigation.products

import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imecatro.demosales.navigation.products.MyProduct.Companion.MODE_CATEGORIES
import com.imecatro.products.ui.categories.screens.CategoriesScreenImpl
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * A Composable that provides an adaptive list-detail layout for products.
 *
 * It uses [NavigableSupportingPaneScaffold] to handle different screen sizes and orientations.
 * In large screens or landscape mode, it can show the list and details (or categories) simultaneously.
 *
 * @param onAddProduct Callback invoked when the user chooses to add a new product.
 * @param onCreateCatalog Callback invoked with a list of product IDs to create a catalog.
 * @param onEditProduct Callback invoked when the user chooses to edit a specific product.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAndDetailsPane(
    onAddProduct: () -> Unit = {},
    onCreateCatalog: (List<Long>) -> Unit = {},
    onEditProduct: (Long) -> Unit = {}
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val directive = calculatePaneScaffoldDirective(adaptiveInfo)
    val customDirective = if (isPortrait) {
        directive.copy( maxVerticalPartitions = 1)
    } else {
        directive
    }

    val navigator = rememberSupportingPaneScaffoldNavigator<MyProduct>(
        scaffoldDirective = customDirective
    )
    val scope = rememberCoroutineScope()
    val viewModel: ProductsDetailsViewModel =
        hiltViewModel(creationCallback = { f: ProductsDetailsViewModel.Factory -> f.create(0L) })

    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane {
                ListOfProductsStateImpl(
                    productsViewModel = hiltViewModel(),
                    onCreateCatalog = onCreateCatalog,
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
            }
        }
    )
}

/**
 * Parcelable class used for navigation within the [ListAndDetailsPane].
 *
 * @property id The ID of the product.
 * @property mode The mode of the pane (e.g., details, categories).
 */
@Parcelize
class MyProduct(val id: Long, val mode: Int = 0) : Parcelable {

    companion object {
        /** Mode for displaying product categories. */
        const val MODE_CATEGORIES = 1
        /** Mode for displaying a catalog. */
        const val MODE_CATALOG = 2
    }
}
