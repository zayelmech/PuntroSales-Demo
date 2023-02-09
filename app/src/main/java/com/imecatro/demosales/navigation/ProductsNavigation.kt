package com.imecatro.demosales.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.details.views.DetailsComposableStateImpl
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import com.imecatro.products.ui.update.views.UpdateProductComposableStateImpl

private const val TAG = "ProductsNavigation"

@Composable
fun ProductsNavigation(
    navController : NavHostController
) {


    NavHost(navController = navController, startDestination = ProductsDestinations.List.route) {
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
            })) { backStackEntry ->
            DetailsComposableStateImpl(
                hiltViewModel(),
                backStackEntry.arguments?.getInt("productId")
            ) {
                it?.let {
                    navController.navigate(ProductsDestinations.Edit.route + "/" + it)
                } ?: run {
                    navController.navigate(ProductsDestinations.List.route) {
                        popUpTo(ProductsDestinations.List.route)
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
            })) { backStackEntry ->

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


sealed class ProductsDestinations(val route: String) {
    object List : ProductsDestinations("Home")
    object Add : ProductsDestinations("Add")
    object Edit : ProductsDestinations("Edit")
    object Details : ProductsDestinations("Details")
}
