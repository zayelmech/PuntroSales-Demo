package com.imecatro.demosales.ui.sales.fulfillment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentOrderUiModel
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentPlanUiState
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentProductUiModel
import com.imecatro.demosales.ui.sales.fulfillment.views.FulfillmentPlanScreen
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class FulfillmentPlanScreenTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val state = FulfillmentPlanUiState(
        isLoading = false, orderCount = 1, clientCount = 1,
        products = listOf(
            FulfillmentProductUiModel(
                1, "Café", "kg", BigDecimal("5"), BigDecimal("3"), BigDecimal("2"),
                listOf(FulfillmentOrderUiModel(101, "María", BigDecimal("5")))
            ),
            FulfillmentProductUiModel(
                2, "Miel", "pz", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, emptyList()
            )
        )
    )

    @Test
    fun shoppingFilterShowsOnlyShortagesAndShareRemainsAvailable() {
        var shares = 0
        compose.setContent {
            PuntroSalesDemoTheme {
                FulfillmentPlanScreen(state, {}, {}, {}, { shares++ })
            }
        }
        compose.onNodeWithText(context.getString(R.string.fulfillment_to_buy, 1)).performClick()
        compose.onNodeWithText("Café").assertIsDisplayed()
        compose.onNodeWithText("Miel").assertDoesNotExist()
        compose.onNodeWithText(context.getString(R.string.fulfillment_share)).performClick()
        compose.runOnIdle { assertEquals(1, shares) }
    }

    @Test
    fun relatedOrderShowsCustomerAndOpensCorrectOrder() {
        var openedOrder = 0L
        compose.setContent {
            PuntroSalesDemoTheme {
                FulfillmentPlanScreen(state, {}, { openedOrder = it }, {}, {})
            }
        }
        compose.onNodeWithText(context.getString(R.string.fulfillment_related_orders, 1))
            .performScrollTo().performClick()
        compose.onNodeWithText("María").performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(101L, openedOrder) }
    }

    @Test
    fun unknownInventoryPreventsSharingAnIncompleteList() {
        val incomplete = state.copy(products = state.products.map { it.copy(available = null, missing = null) })
        compose.setContent {
            PuntroSalesDemoTheme {
                FulfillmentPlanScreen(incomplete, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText(context.getString(R.string.fulfillment_review_notice)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.fulfillment_share)).assertIsNotEnabled()
    }
}
