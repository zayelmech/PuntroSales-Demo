package com.imecatro.demosales.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainNavBar() {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {

        Icon(imageVector = Icons.Filled.Home, contentDescription = null)
        Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = null)

    }
}

@Preview(showBackground = false)
@Composable
fun PreviewMainNavBar() {
    MainNavBar()
}