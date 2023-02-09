package com.imecatro.demosales.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposable
import com.imecatro.demosales.ui.sales.add.views.createFakeListOfProductsOnCart

@Composable
fun SalesNavigation(
    navController : NavHostController
) {
    NavHost(navController = navController, startDestination = SalesDestinations.List.route) {
        composable(SalesDestinations.List.route) {
            CreateTicketComposable(createFakeListOfProductsOnCart(20),{},{}) {}
        }
        composable(SalesDestinations.Add.route) {
            //TODO add sales route
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