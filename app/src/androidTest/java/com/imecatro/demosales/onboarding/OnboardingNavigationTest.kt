package com.imecatro.demosales.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.imecatro.demosales.navigation.products.CatalogDestinations
import com.imecatro.demosales.navigation.products.ProductsDestinations
import com.imecatro.demosales.navigation.profile.ProfileRoute
import com.imecatro.demosales.navigation.sales.SalesDestinations
import com.imecatro.demosales.ui.NavigationDirections
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class OnboardingNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun everyStepUsesTheExistingDestinationAndCatalogReceivesProductIds() {
        lateinit var controller: NavHostController
        compose.setContent {
            controller = rememberNavController()
            NavHost(controller, startDestination = NavigationDirections.ProductsFeature) {
                navigation<NavigationDirections.ProductsFeature>(startDestination = ProductsDestinations.ListAndDetails) {
                    composable<ProductsDestinations.ListAndDetails> { }
                    composable<ProductsDestinations.Add> { }
                    composable<CatalogDestinations.CatalogMaker> { }
                    composable<CatalogDestinations.Management> { }
                }
                navigation<NavigationDirections.ProfileFeature>(startDestination = ProfileRoute) {
                    composable<ProfileRoute> { }
                }
                navigation<NavigationDirections.SalesFeature>(startDestination = SalesDestinations.List) {
                    composable<SalesDestinations.List> { }
                    composable<SalesDestinations.Add> { }
                    composable<SalesDestinations.FulfillmentPlan> { }
                }
            }
        }
        compose.runOnIdle {
            controller.openOnboardingStep(OnboardingStep.CATALOG, listOf(1, 2), false)
            assertTrue(controller.currentDestination!!.hasRoute<CatalogDestinations.CatalogMaker>())
            assertEquals(listOf(1L, 2L), controller.currentBackStackEntry!!.toRoute<CatalogDestinations.CatalogMaker>().ids.toList())
            controller.openOnboardingStep(OnboardingStep.BUSINESS, emptyList(), false)
            assertTrue(controller.currentDestination!!.hasRoute<ProfileRoute>())
            controller.openOnboardingStep(OnboardingStep.PRODUCTS, emptyList(), false)
            assertTrue(controller.currentDestination!!.hasRoute<ProductsDestinations.Add>())
            controller.openOnboardingStep(OnboardingStep.ORDER, emptyList(), false)
            assertTrue(controller.currentDestination!!.hasRoute<SalesDestinations.Add>())
            controller.openOnboardingStep(OnboardingStep.FULFILLMENT, emptyList(), false)
            assertTrue(controller.currentDestination!!.hasRoute<SalesDestinations.FulfillmentPlan>())
            controller.openOnboardingStep(OnboardingStep.CATALOG, emptyList(), true)
            assertTrue(controller.currentDestination!!.hasRoute<CatalogDestinations.Management>())
        }
    }
}
