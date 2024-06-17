package com.imecatro.demosales.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


val appFeatures =
    listOf(NavigationDirections.PRODUCTS, NavigationDirections.SALES, NavigationDirections.CLIENTS)

@Preview(showBackground = true)
@Composable
fun MainBottomBar(
    navController: NavHostController = rememberNavController()
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        appFeatures.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.route
                    )
                },
                label = { Text(text = stringResource(id = screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    if (screen.route !== NavigationDirections.CLIENTS.route)
                        navController.navigate(screen.route) {
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
                    else
                        navController.navigate(ClientsFeature) {
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