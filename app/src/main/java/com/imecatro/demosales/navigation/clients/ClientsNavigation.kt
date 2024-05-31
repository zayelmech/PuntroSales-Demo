package com.imecatro.demosales.navigation.clients

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.clientsNavigation(navController: NavHostController, featureRoute: String) {
    navigation(startDestination = ClientsDestinations.List.route, route = featureRoute) {

        composable(route = ClientsDestinations.List.route) {
            Text(text = "In progress...")
            //TODO
        }
        composable(route = ClientsDestinations.Details.route) {
            Text(text = "In progress...")
            //TODO
        }
        composable(route = ClientsDestinations.Add.route) {
            Text(text = "In progress...")
            //TODO
        }
        composable(route = ClientsDestinations.Edit.route) {
            Text(text = "In progress...")
            //TODO
        }

    }
}