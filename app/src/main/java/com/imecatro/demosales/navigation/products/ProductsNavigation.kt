package com.imecatro.demosales.navigation.products

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.imecatro.demosales.navigation.ListAndDetailsPane
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel
import com.imecatro.products.ui.update.views.UpdateProductComposableStateImpl

private const val TAG = "ProductsNavigation"

inline fun <reified T : Any> NavGraphBuilder.productsNavigation(navController: NavHostController) {
    navigation<T>(startDestination = ProductsDestinations.ListAndDetails) {
        composable<ProductsDestinations.ListAndDetails> {

            ListAndDetailsPane(onAddProduct = {
                navController.navigate(ProductsDestinations.Add)
            }, onEditProduct = { id ->
                navController.navigate(ProductsDestinations.Edit(id))
            })
        }
        composable<ProductsDestinations.List> {
            ListOfProductsStateImpl(productsViewModel = hiltViewModel()) {
                it?.let {
                    navController.navigate(ProductsDestinations.Details(it)) {
                        popUpTo(ProductsDestinations.List)
                    }
                } ?: run {
                    navController.navigate(ProductsDestinations.Add)
                }
            }
        }
        composable<ProductsDestinations.Details> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<ProductsDestinations.Details>()

            val viewModel: ProductsDetailsViewModel =
                hiltViewModel(creationCallback = { factory: ProductsDetailsViewModel.Factory ->
                    factory.create(
                        navArgs.id
                    )
                })

            DetailsComposableStateImpl(
                viewModel,
                onNavigateBack = {
                    navController.navigate(ProductsDestinations.List) {
                        popUpTo(ProductsDestinations.List) { inclusive = true }
                    }
                },
                onProductDeleted = {
                    navController.navigate(ProductsDestinations.List) {
                        popUpTo(ProductsDestinations.List) { inclusive = true }
                    }
                }, onNavigateToEdit = {
                    navController.navigate(ProductsDestinations.Edit(navArgs.id))
                })

        }
        composable<ProductsDestinations.Add> {

            AddProductComposableStateImpl(hiltViewModel()) {
                navController.navigate(ProductsDestinations.List) {
                    popUpTo(ProductsDestinations.List)
                }
            }
        }

        composable<ProductsDestinations.Edit> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<ProductsDestinations.Edit>()

            val viewModel: UpdateProductViewModel =
                hiltViewModel(creationCallback = { factory: UpdateProductViewModel.Factory ->
                    factory.create(
                        navArgs.id
                    )
                })


            UpdateProductComposableStateImpl(viewModel) {
                navController.navigate(ProductsDestinations.List) {
                    popUpTo(ProductsDestinations.List)
                }
            }
        }
    }

}