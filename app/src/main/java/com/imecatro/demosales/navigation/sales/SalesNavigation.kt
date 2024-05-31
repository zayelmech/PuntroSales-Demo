package com.imecatro.demosales.navigation.sales

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.imecatro.demosales.ui.sales.add.views.CheckoutTicketComposableImpl
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposableStateImpl
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl

fun NavGraphBuilder.salesFeature(navController: NavHostController, featureRoute: String) {
    navigation(startDestination = SalesDestinations.List.route, route = featureRoute) {
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
                navController.navigate(SalesDestinations.List.route) {
                    popUpTo(SalesDestinations.List.route) { inclusive = true }
                }
            }, onNavigateToCheckout = {
                navController.navigate(SalesDestinations.Checkout.route)
            })
        }

        composable(SalesDestinations.Checkout.route) {

            CheckoutTicketComposableImpl(checkoutViewModel = hiltViewModel()) {
                navController.navigate(SalesDestinations.List.route) {
                    popUpTo(SalesDestinations.List.route) { inclusive = true }
                }
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

            TicketDetailsComposableImpl(ticketDetailsVM = hiltViewModel(), saleId = id!!.toLong()) {
                it?.let {
                    navController.navigate(SalesDestinations.Add.route) {
                        popUpTo("${SalesDestinations.Details.route}/${id}") { inclusive = true }
                    }
                } ?: run {
                    navController.navigate(SalesDestinations.List.route) {
                        popUpTo(SalesDestinations.List.route) { inclusive = true }
                    }
                }
            }
        }
    }
}