package com.imecatro.demosales.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.newSaleGraph(
    navController: NavController
) {

    navigation(startDestination = "username", route = "login") {
        composable("username") {

        }
    }
}


//sealed class NewSaleDestinations(val route : String){
//
//    object Login :
//
//}