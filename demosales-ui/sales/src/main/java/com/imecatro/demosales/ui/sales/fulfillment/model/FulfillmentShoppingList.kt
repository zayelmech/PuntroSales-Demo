package com.imecatro.demosales.ui.sales.fulfillment.model

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal fun BigDecimal.formatQuantity(locale: Locale = Locale.getDefault()): String =
    NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = maxOf(0, this@formatQuantity.stripTrailingZeros().scale())
        isGroupingUsed = false
    }.format(this)

/** Only purchase quantities are shared; customer names and order details stay in the app. */
internal fun FulfillmentPlanUiState.shoppingListText(
    title: String,
    locale: Locale = Locale.getDefault()
): String {
    if (!canShare) return ""
    return buildString {
        appendLine(title)
        appendLine()
        shoppingList.forEach { product ->
            appendLine("• ${product.name}: ${product.missing!!.formatQuantity(locale)} ${product.unit}")
        }
    }.trimEnd()
}
