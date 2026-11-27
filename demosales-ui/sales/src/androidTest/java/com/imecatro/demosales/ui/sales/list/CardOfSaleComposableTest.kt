package com.imecatro.demosales.ui.sales.list

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.views.CardOfSaleComposable
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import java.util.Locale
import org.junit.Rule
import org.junit.Test

class CardOfSaleComposableTest {
    @get:Rule val compose = createComposeRule()

    @Test fun formattedTotalUpdatesWhenAmountCurrencyOrLocaleChanges() {
        val sale = mutableStateOf(
            SaleOnListUiModel(1, "Client", "2026-11-10T12:00:00Z", 10.0, "Completed", Color.Green)
        )
        val locale = mutableStateOf(Locale.US)
        val currency = mutableStateOf("USD")
        compose.setContent {
            CompositionLocalProvider(LocalCurrencyCode provides currency.value) {
                MaterialTheme { CardOfSaleComposable(sale = sale.value, locale = locale.value) }
            }
        }
        compose.onNodeWithText("$10.00").assertExists()
        compose.runOnIdle { sale.value = sale.value.copy(total = 20.0) }
        compose.onNodeWithText("$20.00").assertExists()
        compose.runOnIdle { currency.value = "EUR" }
        compose.onNodeWithText("€20.00").assertExists()
        compose.runOnIdle { locale.value = Locale.GERMANY }
        compose.onNodeWithText("20,00\u00a0€").assertExists()
    }
}
