package com.imecatro.demosales.products

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.products.ui.list.components.ProductCardCompose
import com.imecatro.products.ui.list.model.ProductUiModel
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class ProductsPresentationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun cachedPriceRespondsToAmountCurrencyAndLocaleChanges() {
        val price = mutableStateOf("1234.50")
        val currency = mutableStateOf("USD")
        val configuration = mutableStateOf(Configuration(
            InstrumentationRegistry.getInstrumentation().targetContext.resources.configuration
        ).apply { setLocales(LocaleList(Locale.US)) })

        compose.setContent {
            PuntroSalesDemoTheme {
                CompositionLocalProvider(
                    LocalConfiguration provides configuration.value,
                    LocalCurrencyCode provides currency.value
                ) {
                    ProductCardCompose(product = ProductUiModel("Coffee").copy(price = price.value))
                }
            }
        }
        compose.onNodeWithText(Money.format("1234.50", Locale.US, "USD")).assertIsDisplayed()
        compose.runOnIdle { price.value = "42.25" }
        compose.onNodeWithText(Money.format("42.25", Locale.US, "USD")).assertIsDisplayed()
        compose.runOnIdle { currency.value = "EUR" }
        compose.onNodeWithText(Money.format("42.25", Locale.US, "EUR")).assertIsDisplayed()
        compose.runOnIdle {
            configuration.value = Configuration(configuration.value).apply {
                setLocales(LocaleList(Locale.GERMANY))
            }
        }
        compose.onNodeWithText(Money.format("42.25", Locale.GERMANY, "EUR")).assertIsDisplayed()
    }
}
