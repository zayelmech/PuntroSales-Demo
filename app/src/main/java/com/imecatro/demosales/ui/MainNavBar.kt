package com.imecatro.demosales.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainNavBar(
    onNavigateToRoute: (NavigationDirections) -> Unit,
    selected: NavigationDirections
) {

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            label = { Text(NavigationDirections.PRODUCTS.route) },
            selected = selected == NavigationDirections.PRODUCTS,
            onClick = { onNavigateToRoute(NavigationDirections.PRODUCTS) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
            label = { Text(NavigationDirections.SALES.route) },
            selected = selected == NavigationDirections.SALES,
            onClick = {onNavigateToRoute(NavigationDirections.SALES)}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
            label = { Text(NavigationDirections.CLIENTS.route) },
            selected = selected == NavigationDirections.CLIENTS,
            onClick = {onNavigateToRoute(NavigationDirections.CLIENTS)}
        )
    }

}

enum class NavigationDirections(val route: String) {
    PRODUCTS("Inventory"),
    SALES("Sales"),
    CLIENTS("Clients")
}

@Preview(showBackground = false)
@Composable
fun PreviewMainNavBar() {
    MainNavBar({}, NavigationDirections.SALES)
}