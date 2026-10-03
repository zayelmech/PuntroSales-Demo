package com.imecatro.demosales.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.list.views.ListOfProducts
import com.imecatro.products.ui.list.views.fakeProductsList
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class OnboardingUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun laterDismissesIntroductionWithoutCompletingSteps() {
        val visible = mutableStateOf(true)
        compose.setContent {
            PuntroSalesDemoTheme {
                OnboardingBanner(OnboardingProgress(), {}, {})
                if (visible.value) OnboardingIntroduction({}, { visible.value = false })
            }
        }
        compose.onNodeWithTag("onboarding-later").performScrollTo().performClick()
        compose.onNodeWithTag("onboarding-introduction").assertDoesNotExist()
        compose.onNodeWithTag("onboarding-banner").assertIsDisplayed()
    }

    @Test fun startOpensChecklistAndAllowsLastStepFirst() {
        val intro = mutableStateOf(true)
        val checklist = mutableStateOf(false)
        var selected: OnboardingStep? = null
        compose.setContent {
            PuntroSalesDemoTheme {
                if (intro.value) OnboardingIntroduction({ intro.value = false; checklist.value = true }, {})
                if (checklist.value) OnboardingChecklist(OnboardingProgress(), { selected = it }, {})
            }
        }
        compose.onNodeWithTag("onboarding-start").performScrollTo().performClick()
        compose.onNodeWithTag("onboarding-steps").performScrollToNode(hasTestTag("onboarding-step-catalog"))
        compose.onNodeWithTag("onboarding-step-catalog").performClick()
        compose.runOnIdle { assertEquals(OnboardingStep.CATALOG, selected) }
    }

    @Test fun bannerRemainsFixedAboveNavigationWhileProductsScrollAndDisappearsOnCompletion() {
        val progress = mutableStateOf(OnboardingProgress())
        compose.setContent {
            PuntroSalesDemoTheme {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        ListOfProducts(list = fakeProductsList(100), banner = { OnboardingBanner(progress.value, {}, {}) })
                    }
                    Surface(Modifier.fillMaxWidth().height(80.dp).testTag("navigation")) { Text("Navigation") }
                }
            }
        }
        val before = compose.onNodeWithTag("onboarding-banner").fetchSemanticsNode().boundsInRoot
        val navigation = compose.onNodeWithTag("navigation").fetchSemanticsNode().boundsInRoot
        assertTrue(before.bottom <= navigation.top)
        compose.onAllNodes(hasScrollAction()).onFirst().performScrollToIndex(90)
        assertEquals(before, compose.onNodeWithTag("onboarding-banner").fetchSemanticsNode().boundsInRoot)
        compose.runOnIdle { progress.value = OnboardingProgress(steps = OnboardingStep.entries.toSet(), completed = true) }
        compose.onNodeWithTag("onboarding-banner").assertDoesNotExist()
    }

    @Test fun emptyInventoryAndLargeFontsInDarkThemeKeepBannerActionsAccessible() {
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, 1.6f)) {
                PuntroSalesDemoTheme(darkTheme = true) {
                    ListOfProducts(list = emptyList(), banner = { OnboardingBanner(OnboardingProgress(), {}, {}) })
                }
            }
        }
        compose.onNodeWithTag("onboarding-banner").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-continue").assertIsDisplayed().performClick()
        compose.onNodeWithTag("onboarding-checklist").assertIsDisplayed().performClick()
    }

    @Test fun emptySlotDoesNotAddBannerOrBlockInventory() {
        compose.setContent { PuntroSalesDemoTheme { ListOfProducts(list = fakeProductsList(50)) } }
        compose.onNodeWithTag("onboarding-banner").assertDoesNotExist()
        compose.onAllNodes(hasScrollAction()).onFirst().performScrollToIndex(49)
        compose.onNodeWithText("Product Name 50").assertIsDisplayed()
    }
}
