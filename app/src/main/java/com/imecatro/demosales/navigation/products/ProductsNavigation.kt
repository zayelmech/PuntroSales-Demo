package com.imecatro.demosales.navigation.products

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import com.imecatro.products.ui.update.views.UpdateProductComposableStateImpl

private const val TAG = "ProductsNavigation"

inline fun <reified T: Any> NavGraphBuilder.productsNavigation(navController: NavHostController) {
    navigation<T>(startDestination = ProductsDestinations.List) {
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
            DetailsComposableStateImpl(
                hiltViewModel(),
                navArgs.id
            ) {
                it?.let {
                    navController.navigate(ProductsDestinations.Edit(it))
                } ?: run {
                    navController.navigate(ProductsDestinations.List) {
                        popUpTo(ProductsDestinations.List) { inclusive = true }
                    }
                }

            }

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
            UpdateProductComposableStateImpl(
                updateProductViewModel = hiltViewModel(),
                productId = navArgs.id
            ) {
                navController.navigate(ProductsDestinations.List) {
                    popUpTo(ProductsDestinations.List)
                }
            }
        }
    }

}