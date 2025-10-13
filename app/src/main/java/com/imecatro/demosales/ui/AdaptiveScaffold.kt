package com.imecatro.demosales.ui

import androidx.annotation.Keep
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.imecatro.demosales.navigation.clients.clientsNavigation
import com.imecatro.demosales.navigation.products.productsNavigation
import com.imecatro.demosales.navigation.sales.salesFeature

@Keep
enum class AppDestinations {
    PRODUCTS, SALES, CLIENTS
}

fun NavigationSuiteScope.adaptiveNavigationBar(
    currentDestination: NavDestination?,
    onCurrentDestinationChanged: (AppDestinations) -> Unit
) {
    AppDestinations.entries.forEach { destination ->
        val screen = destination.toRoute()
        item(
            icon = {
                Icon(
                    painterResource(screen.icon),
                    contentDescription = stringResource(screen.tittle)
                )
            },
            label = { Text(stringResource(screen.tittle)) },
            selected = currentDestination?.hierarchy?.any { it.route?.contains(screen::class.qualifiedName.toString()) ?: false } == true,
            onClick = { onCurrentDestinationChanged(destination) }
        )
    }
}

fun AppDestinations.toRoute(): ParentFeature {
    return when (this) {
        AppDestinations.PRODUCTS -> NavigationDirections.ProductsFeature
        AppDestinations.SALES -> NavigationDirections.SalesFeature
        AppDestinations.CLIENTS -> NavigationDirections.ClientsFeature
    }
}

fun NavHostController.navigateToRoot(route: ParentFeature) {
    this.navigate(route) {
        popUpTo(this@navigateToRoot.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppAdaptiveNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val adaptiveInfo = currentWindowAdaptiveInfo()
    // Custom configuration that shows a navigation drawer in large screens.
    val customNavSuiteType =
        with(adaptiveInfo) {
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
            }
        }

    NavigationSuiteScaffold(
        layoutType = customNavSuiteType,
        navigationSuiteItems = {
            adaptiveNavigationBar(currentDestination) { newDestination ->
                when (newDestination) {
                    AppDestinations.PRODUCTS -> navController.navigateToRoot(NavigationDirections.ProductsFeature)
                    AppDestinations.SALES -> navController.navigateToRoot(NavigationDirections.SalesFeature)
                    AppDestinations.CLIENTS -> navController.navigateToRoot(NavigationDirections.ClientsFeature)
                }
            }
        }) {
        NavHost(
            navController = navController,
            startDestination = NavigationDirections.ProductsFeature
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