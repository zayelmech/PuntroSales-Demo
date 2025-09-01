package com.imecatro.demosales.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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

    val ss = navController.currentBackStackEntryAsState()

    AppCustomScaffold(
        bottomBar = {
            val showBottomBar =
                ss.value?.destination?.route?.contains("List") ?: false
            if (showBottomBar)
                MainBottomBar(navController)
        }
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


@Composable
fun AppCustomScaffold(
    bottomBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit // Content now accepts PaddingValues
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        // Create references for the composables to constrain
        val (contentRef, bottomBarRef) = createRefs()

        // Content area
        Box(modifier = Modifier.constrainAs(contentRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(bottomBarRef.top) // Constrain bottom of content to top of bottomBar
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints // Content fills available space
        }) {
            // Here, we need to decide what PaddingValues to pass.
            // If the bottomBar has a known or estimated height, we can create padding for it.
            // This is a simplification. Scaffold does this much more robustly.
            // Let's assume a fixed bottom bar height for this example.
            //val bottomBarHeight = 80.dp // Example: typical bottom navigation height
            content(PaddingValues(bottom = 0.dp))
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .constrainAs(bottomBarRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent // Or a fixed height
                }
                //.border(1.dp, color = Color.Red)
                .sizeIn(minHeight = 20.dp)) {
            bottomBar()
        }
    }
}