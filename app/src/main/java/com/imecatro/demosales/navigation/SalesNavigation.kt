package com.imecatro.demosales.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposable
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposableStateImpl
import com.imecatro.demosales.ui.sales.add.views.createFakeListOfProductsOnCart
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl

@Composable
fun SalesNavigation(
    navController : NavHostController
) {
    NavHost(navController = navController, startDestination = SalesDestinations.List.route) {
        composable(SalesDestinations.List.route) {
            SalesListComposableStateImpl(salesListViewModel = hiltViewModel()){

            }
        }
        composable(SalesDestinations.Add.route) {
            CreateTicketComposableStateImpl(addSaleViewModel = hiltViewModel())
        }
        composable(SalesDestinations.Edit.route) {
            //TODO edit sales route

        }
        composable(SalesDestinations.Details.route) {
            //TODO details sales route

        }
    }
}

sealed class SalesDestinations(val route: String) {
    object List : SalesDestinations("List")
    object Add : SalesDestinations("Add")
    object Edit : SalesDestinations("Edit")
    object Details : SalesDestinations("Details")
}