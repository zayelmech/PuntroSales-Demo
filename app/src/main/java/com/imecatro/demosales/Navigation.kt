package com.imecatro.demosales

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imecatro.products.ui.add.viewmodel.AddViewModel
import com.imecatro.products.ui.add.views.AddProductComposableStateImpl
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl

private const val TAG = "ProductsNavigation"

@Composable
fun ProductsNavigation(productsViewModel: ProductsViewModel, addProductViewModel: AddViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ProductsDestinations.List.route) {
        composable(ProductsDestinations.List.route) {
            ListOfProductsStateImpl(productsViewModel) {
                it?.let {
                    Log.d(TAG, "Product ID: $it --EDIT REQUEST")
                } ?: run {
                    navController.navigate(ProductsDestinations.Add.route)
                    Log.d(TAG, "Product ID: --ADD REQUEST")
                }
            }
        }
        composable(ProductsDestinations.Add.route) {

            AddProductComposableStateImpl(addProductViewModel) {
                navController.navigate(ProductsDestinations.List.route){
                    popUpTo(ProductsDestinations.List.route)
                }
            }
        }

    }

}


sealed class ProductsDestinations(val route: String) {
    object List : ProductsDestinations("Home")
    object Add : ProductsDestinations("Add")

}