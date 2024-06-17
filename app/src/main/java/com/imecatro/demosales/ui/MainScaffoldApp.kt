package com.imecatro.demosales.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.imecatro.demosales.navigation.clients.clientsNavigation
import com.imecatro.demosales.navigation.products.productsNavigation
import com.imecatro.demosales.navigation.sales.salesFeature

@Preview(showBackground = true)
@Composable
fun MainScaffoldApp() {

    // navController
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { MainBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavigationDirections.ProductsFeature,
            modifier = Modifier.padding(paddingValues = padding)
        ) {
            // We can add, see, edit or delete any product
            productsNavigation<NavigationDirections.ProductsFeature>(navController)
            // We can add, see, edit or delete any sale
            salesFeature<NavigationDirections.SalesFeature>(navController)
            // We can add, see, edit or delete any client
            clientsNavigation<NavigationDirections.ClientsFeature>(navController)
        }
    }
}