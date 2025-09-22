package com.imecatro.demosales.navigation.sales

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.imecatro.demosales.ui.sales.add.screens.CheckoutTicketComposableImpl
import com.imecatro.demosales.ui.sales.add.screens.CreateTicketComposableStateImpl
import com.imecatro.demosales.ui.sales.add.screens.ResumeTicketScreenImpl
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.sales.details.views.TicketDetailsComposableImpl

inline fun <reified T : Any> NavGraphBuilder.salesFeature(navController: NavHostController) {
    navigation<T>(startDestination = SalesDestinations.List) {
        composable<SalesDestinations.List> {

            ListAndDetailsSalesPane(
                onAddSale = { navController.navigate(SalesDestinations.Add()) },
                onEditSale = { id ->
                    navController.navigate(SalesDestinations.Checkout(id)) {
                        popUpTo(SalesDestinations.Details(id)) { inclusive = true }
                    }
                },
                onDuplicateSale = { id ->
                    navController.navigate(SalesDestinations.Add(id)) {
                        popUpTo(SalesDestinations.Details(id)) { inclusive = true }
                    }
                }
            )
        }
        composable<SalesDestinations.Add> { backStackEntry ->
            val id = backStackEntry.toRoute<SalesDestinations.Add>().id

            CreateTicketComposableStateImpl(
                addSaleViewModel = hiltViewModel(creationCallback = { f: AddSaleViewModel.Factory -> f.create(id ?: 0L) }),
                onBackToList = {
                    navController.navigate(SalesDestinations.List) {
                        popUpTo(SalesDestinations.List) { inclusive = true }
                    }
                },
                onNavigateToCheckout = { id ->
                    navController.navigate(SalesDestinations.Checkout(id))
                })
        }

        composable<SalesDestinations.Checkout> { backStackEntry ->

            val id = backStackEntry.toRoute<SalesDestinations.Checkout>().id

            CheckoutTicketComposableImpl(
                checkoutViewModel = hiltViewModel(creationCallback = { f: CheckoutViewModel.Factory -> f.create(id) })
            ) { ticket ->
                navController.navigate(SalesDestinations.SuccessDetails(ticket)) {
                    popUpTo(SalesDestinations.List) { inclusive = false }
                }
            }
        }
        composable<SalesDestinations.SuccessDetails> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<SalesDestinations.Details>()

            val viewModel: TicketDetailsViewModel =
                hiltViewModel(creationCallback = { f: TicketDetailsViewModel.Factory -> f.create(navArgs.id) })

            ResumeTicketScreenImpl(ticketDetailsVM = viewModel, saleId = navArgs.id) {
                it?.let {
                    navController.navigate(SalesDestinations.Add()) {
                        popUpTo(SalesDestinations.Details(navArgs.id)) { inclusive = true }
                    }
                } ?: run {
                    navController.navigate(SalesDestinations.List) {
                        popUpTo(SalesDestinations.List) { inclusive = true }
                    }
                }
            }

        }
        composable<SalesDestinations.Details> { backStackEntry ->
            val navArgs = backStackEntry.toRoute<SalesDestinations.Details>()

            val viewModel: TicketDetailsViewModel =
                hiltViewModel(creationCallback = { f: TicketDetailsViewModel.Factory -> f.create(navArgs.id) })

            TicketDetailsComposableImpl(
                ticketDetailsVM = viewModel, saleId = navArgs.id,
                onEditTicket = {
                    navController.navigate(SalesDestinations.Checkout(it)) {
                        popUpTo(SalesDestinations.Details(navArgs.id)) { inclusive = true }
                    }
                },
                onDuplicateTicket = {
                    navController.navigate(SalesDestinations.Add(it)) {
                        popUpTo(SalesDestinations.Details(navArgs.id)) { inclusive = true }
                    }
                },
                onBackToList = {
                    navController.navigate(SalesDestinations.List) {
                        popUpTo(SalesDestinations.List) { inclusive = true }
                    }
                }
            )
        }
    }
}