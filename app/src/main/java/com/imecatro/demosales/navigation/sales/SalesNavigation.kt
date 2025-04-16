package com.imecatro.demosales.navigation.sales

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.imecatro.demosales.ui.sales.add.views.CheckoutTicketComposableImpl
import com.imecatro.demosales.ui.sales.add.views.CreateTicketComposableStateImpl
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl

inline fun <reified T : Any> NavGraphBuilder.salesFeature(navController: NavHostController) {
    navigation<T>(startDestination = SalesDestinations.List) {
        composable<SalesDestinations.List> {
            SalesListComposableStateImpl(salesListViewModel = hiltViewModel()) {
                it?.let {
                    navController.navigate(SalesDestinations.Details(it)) {
                        popUpTo(SalesDestinations.List)
                    }
                } ?: run {
                    navController.navigate(SalesDestinations.Add)
                }
            }
        }
        composable<SalesDestinations.Add> {

            CreateTicketComposableStateImpl(addSaleViewModel = hiltViewModel(),
                onNavigateToCheckout = {
                    navController.navigate(SalesDestinations.Checkout(1))
                })
        }

        composable<SalesDestinations.Checkout> {

            CheckoutTicketComposableImpl(checkoutViewModel = hiltViewModel()) {
                navController.navigate(SalesDestinations.List) {
                    popUpTo(SalesDestinations.List) { inclusive = true }
                }
            }
        }
        composable<SalesDestinations.Edit> {
            //TODO edit sales route

        }
        composable<SalesDestinations.Details> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<SalesDestinations.Details>()

            TicketDetailsComposableImpl(ticketDetailsVM = hiltViewModel(), saleId = navArgs.id) {
                it?.let {
                    navController.navigate(SalesDestinations.Add) {
                        popUpTo(SalesDestinations.Details(navArgs.id)) { inclusive = true }
                    }
                } ?: run {
                    navController.navigate(SalesDestinations.List) {
                        popUpTo(SalesDestinations.List) { inclusive = true }
                    }
                }
            }
        }
    }
}