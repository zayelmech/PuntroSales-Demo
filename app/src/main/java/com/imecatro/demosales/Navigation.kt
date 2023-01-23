package com.imecatro.demosales

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imecatro.ui.add.viewmodel.AddViewModel
import com.imecatro.ui.products.viewmodels.ProductsViewModel
import com.imecatro.ui.add.views.AddProductComposable
import com.imecatro.ui.add.views.AddProductComposableStateImpl
import com.imecatro.ui.products.views.ListOfProductsStateImpl

private const val TAG = "ProductsNavigation"

@Composable
fun ProductsNavigation(productsViewModel: ProductsViewModel, addProductViewModel: AddViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ProductsDestinations.List.name) {
        composable(ProductsDestinations.List.name) {
            ListOfProductsStateImpl(productsViewModel) {
                it?.let {
                    Log.d(TAG, "Product ID: $it --EDIT REQUEST")
                } ?: run {
                    navController.navigate(ProductsDestinations.Add.name)
                    Log.d(TAG, "Product ID: --ADD REQUEST")
                }
            }
        }
        composable(ProductsDestinations.Add.name) {

            AddProductComposableStateImpl(addProductViewModel) {
                navController.navigate(ProductsDestinations.List.name)
            }
        }

    }

}


sealed class ProductsDestinations(val name: String) {
    object List : ProductsDestinations("Home")
    object Add : ProductsDestinations("Add")

}