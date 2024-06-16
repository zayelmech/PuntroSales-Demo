package com.imecatro.demosales.navigation.clients

import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.imecatro.demosales.ui.clients.add.views.AddClientComposableImpl
import com.imecatro.demosales.ui.clients.list.views.ClientListImpl

fun NavGraphBuilder.clientsNavigation(navController: NavHostController, featureRoute: String) {
    navigation(startDestination = ClientsDestinations.List.route, route = featureRoute) {

        composable(route = ClientsDestinations.List.route) {
            ClientListImpl(hiltViewModel()) { clientId ->
                clientId?.let {
                   navController.navigate(ClientsDestinations.Details.route)
                }?:run {
                    navController.navigate(ClientsDestinations.Add.route)
                }
            }
        }
        composable(route = ClientsDestinations.Details.route) {
            Text(text = "In progress...")
            //TODO
        }
        composable(route = ClientsDestinations.Add.route) {
            AddClientComposableImpl(hiltViewModel()){
                navController.navigate(ClientsDestinations.List.route)
            }
        }
        composable(route = ClientsDestinations.Edit.route) {
            Text(text = "In progress...")
            //TODO
        }

    }
}