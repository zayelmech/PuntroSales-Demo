package com.imecatro.demosales.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.imecatro.demosales.navigation.clients.clientsNavigation
import com.imecatro.demosales.navigation.products.productsNavigation
import com.imecatro.demosales.navigation.sales.salesFeature

@Preview(showBackground = true)
@Composable
fun MainScaffoldApp() {

    // navController
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(key1 = navBackStackEntry) {
        Log.d("MainScaffoldApp", "navBackStackEntry: $navBackStackEntry")
    }
    Scaffold(
        bottomBar = { MainBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavigationDirections.PRODUCTS.route,
            modifier = Modifier.padding(paddingValues = padding)
        ) {
            // We can add, see, edit or delete any product
            productsNavigation(navController, featureRoute = NavigationDirections.PRODUCTS.route)
            // We can add, see, edit or delete any sale
            salesFeature(navController, featureRoute = NavigationDirections.SALES.route)
            // We can add, see, edit or delete any client
            clientsNavigation(navController, featureRoute = NavigationDirections.CLIENTS.route)
        }
    }
}