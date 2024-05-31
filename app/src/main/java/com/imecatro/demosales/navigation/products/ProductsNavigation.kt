package com.imecatro.demosales.navigation.products

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import com.imecatro.products.ui.update.views.UpdateProductComposableStateImpl

private const val TAG = "ProductsNavigation"

fun NavGraphBuilder.productsNavigation(navController: NavHostController, featureRoute: String) {
    navigation(startDestination = ProductsDestinations.List.route, route = featureRoute) {
        composable(ProductsDestinations.List.route) {
            ListOfProductsStateImpl(productsViewModel = hiltViewModel()) {
                it?.let {
                    navController.navigate(ProductsDestinations.Details.route + "/" + it) {
                        popUpTo(ProductsDestinations.List.route)
                    }
                    Log.d(TAG, "Product ID: $it --EDIT REQUEST")
                } ?: run {
                    navController.navigate(ProductsDestinations.Add.route)
                    Log.d(TAG, "Product ID: --ADD REQUEST")
                }
            }
        }
        composable(
            "${ProductsDestinations.Details.route}/{productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            DetailsComposableStateImpl(
                hiltViewModel(),
                backStackEntry.arguments?.getInt("productId")
            ) {
                it?.let {
                    navController.navigate(ProductsDestinations.Edit.route + "/" + it)
                } ?: run {
                    navController.navigate(ProductsDestinations.List.route) {
                        popUpTo(ProductsDestinations.List.route) { inclusive = true }
                    }
                }

            }

        }
        composable(ProductsDestinations.Add.route) {

            AddProductComposableStateImpl(hiltViewModel()) {
                navController.navigate(ProductsDestinations.List.route) {
                    popUpTo(ProductsDestinations.List.route)
                }
            }
        }

        composable(
            "${ProductsDestinations.Edit.route}/{productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            UpdateProductComposableStateImpl(
                updateProductViewModel = hiltViewModel(),
                productId = backStackEntry.arguments?.getInt("productId")
            ) {
                navController.navigate(ProductsDestinations.List.route) {
                    popUpTo(ProductsDestinations.List.route)
                }
            }
        }
    }

}