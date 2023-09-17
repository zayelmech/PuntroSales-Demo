package com.imecatro.demosales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.imecatro.demosales.navigation.ProductsNavigation
import com.imecatro.demosales.navigation.SalesNavigation


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScaffoldApp() {
    var featureSelected by remember {
        mutableStateOf(NavigationDirections.SALES)
    }
    val productsNavController = rememberNavController()
    val salesNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            MainNavBar(
                onNavigateToRoute = { featureSelected = it },
                featureSelected
            )
        }
    ) {
        Column(
            modifier = Modifier
                .consumeWindowInsets(it)
                .padding(it)
        ) {

            when (featureSelected) {
                NavigationDirections.PRODUCTS -> {
                    ProductsNavigation(productsNavController)
                }

                NavigationDirections.SALES -> {
                    SalesNavigation(salesNavController)
                }

                NavigationDirections.CLIENTS -> {
                    //TODO feature CLIENTS
                }
            }
        }

    }
}