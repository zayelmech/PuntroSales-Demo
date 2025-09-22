package com.imecatro.demosales.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


val appFeatures =
    listOf(NavigationDirections.ProductsFeature, NavigationDirections.SalesFeature, NavigationDirections.ClientsFeature)

/**
 * Composable function that displays the main bottom navigation bar of the application.
 *
 * This bottom bar allows users to navigate between the main features of the app:
 * Products, Sales, and Clients. It highlights the currently selected feature and
 * handles navigation logic, including popping up to the start destination to avoid
 * a large back stack and ensuring a single top instance of each destination.
 *
 * @param navController The [NavHostController] used for navigation between screens.
 *                      Defaults to a new [NavHostController] if not provided.
 */
@Preview(showBackground = true)
@Composable
fun MainBottomBar(
    navController: NavHostController = rememberNavController()
) {
    NavigationBar(contentColor = Color.Transparent, containerColor = Color.Transparent) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        appFeatures.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = screen.tittle)) },
                selected = currentDestination?.hierarchy?.any { it.route?.contains("${screen::class.simpleName}")?:false } == true,
                onClick = {
                    navController.navigate(screen) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true

                    }

                }
            )
        }
    }
}