package com.imecatro.demosales.navigation.clients

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.imecatro.demosales.ui.clients.add.views.AddClientComposableImpl
import com.imecatro.demosales.ui.clients.details.viewmodel.ClientDetailsViewModel
import com.imecatro.demosales.ui.clients.details.views.ClientDetailsComposableImpl
import com.imecatro.demosales.ui.clients.edit.viewmodel.EditClientViewModel
import com.imecatro.demosales.ui.clients.edit.views.EditClientComposableImpl
import com.imecatro.demosales.ui.clients.list.views.ClientListImpl

inline fun <reified T : Any> NavGraphBuilder.clientsNavigation(navController: NavHostController) {
    navigation<T>(startDestination = ClientsList) {

        composable<ClientsList> {
            ClientListImpl(hiltViewModel()) { clientId ->
                clientId?.let {
                    navController.navigate(ClientDetails(clientId)) {
                        popUpTo(ClientsList)
                    }
                } ?: run {
                    navController.navigate(AddClient)
                }
            }
        }
        composable<ClientDetails> { backStackEntry ->
            val navArgs: ClientDetails = backStackEntry.toRoute()
            val viewModel: ClientDetailsViewModel =
                hiltViewModel(creationCallback = { factory: ClientDetailsViewModel.Factory ->
                    factory.create(navArgs.id)
                })

            ClientDetailsComposableImpl(
                viewModel,
                onBackToList = {
                    navController.navigate(ClientsList) {
                        popUpTo(ClientsList) { inclusive = true }
                    }
                },
                onEditClicked = { navController.navigate(EditClient(navArgs.id)) })
        }
        composable<AddClient> {
            AddClientComposableImpl(hiltViewModel(), onBackToList = {
                navController.navigate(ClientsList) {
                    popUpTo(ClientsList) { inclusive = true }
                }
            }) {
                navController.navigate(ClientsList) {
                    popUpTo(ClientsList) { inclusive = true }
                }
            }
        }
        composable<EditClient> { backStackEntry ->
            val navArgs: EditClient = backStackEntry.toRoute()

            val viewModel: EditClientViewModel =
                hiltViewModel(creationCallback = { factory: EditClientViewModel.Factory ->
                    factory.create(navArgs.id)
                })

            EditClientComposableImpl(viewModel) {
                navController.navigate(ClientsList) {
                    popUpTo(ClientsList) { inclusive = true }
                }
            }
        }

    }
}