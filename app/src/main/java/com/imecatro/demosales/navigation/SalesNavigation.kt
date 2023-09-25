package com.imecatro.demosales.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposableStateImpl
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl

@Composable
fun SalesNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = SalesDestinations.List.route) {
        composable(SalesDestinations.List.route) {
            SalesListComposableStateImpl(salesListViewModel = hiltViewModel()) {
                it?.let {
                    navController.navigate(SalesDestinations.Details.route + "/" + it) {
                        popUpTo(SalesDestinations.List.route)
                    }
                } ?: run {
                    navController.navigate(SalesDestinations.Add.route)
                }
            }
        }
        composable(SalesDestinations.Add.route) {

            CreateTicketComposableStateImpl(addSaleViewModel = hiltViewModel(), onSavedTicked = {

            }) {
                //Create Request Update Delete
            }
        }
        composable(SalesDestinations.Edit.route) {
            //TODO edit sales route

        }
        composable(
            "${SalesDestinations.Details.route}/{productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productId")

            TicketDetailsComposableImpl(ticketDetailsVM = hiltViewModel(), saleId = id!!.toLong())
        }
    }
}

sealed class SalesDestinations(val route: String) {
    object List : SalesDestinations("List")
    object Add : SalesDestinations("Add")
    object Edit : SalesDestinations("Edit")
    object Details : SalesDestinations("Details")
}