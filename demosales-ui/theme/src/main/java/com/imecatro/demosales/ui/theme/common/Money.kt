package com.imecatro.demosales.ui.theme.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Money {
    fun format(amount: String, locale: Locale = Locale.getDefault(), currencyCode: String? = null): String {
        val formatLocale = getFormatLocale(locale, currencyCode)
        val nf = NumberFormat.getCurrencyInstance(formatLocale)
        
        // 1. Establecer la moneda primero
        currencyCode?.let {
            try {
                nf.currency = Currency.getInstance(it)
            } catch (e: Exception) {
                // Fallback
            }
        }

        // 2. Forzar los símbolos después de establecer la moneda para evitar que se sobrescriban
        if (nf is DecimalFormat && isLatamStyle(currencyCode ?: nf.currency?.currencyCode)) {
            applyLatamSymbols(nf)
        }

        return nf.format(amount.toDoubleOrNull() ?: 0.0)
    }
    @Deprecated("bad behavior")
    fun format(amount: Double, locale: Locale = Locale.getDefault(), currencyCode: String? = null): String {
        val formatLocale = getFormatLocale(locale, currencyCode)
        val nf = NumberFormat.getCurrencyInstance(formatLocale)

        currencyCode?.let {
            try {
                nf.currency = Currency.getInstance(it)
            } catch (e: Exception) {
            }
        }

        if (nf is DecimalFormat && isLatamStyle(currencyCode ?: nf.currency?.currencyCode)) {
            applyLatamSymbols(nf)
        }

        return nf.format(amount)
    }

    fun getCurrency(locale: Locale): Currency {
        return try {
            Currency.getInstance(locale)
        } catch (e: Exception) {
            Currency.getInstance("USD")
        }
    }

    fun toDouble(
        formatted: String,
        locale: Locale = Locale.getDefault(),
        currency: Currency = getCurrency(locale),
        fractionDigits: Int = currency.defaultFractionDigits.let { if (it < 0) 2 else it }
    ): Double {
        if (formatted.isBlank()) return 0.0
        val digits = formatted.filter(Char::isDigit)
        if (digits.isEmpty()) return 0.0
        return BigDecimal(digits)
            .movePointLeft(fractionDigits)
            .toDouble()
    }
}

private fun isLatamStyle(currencyCode: String?): Boolean {
    return currencyCode in listOf("MXN", "USD", "GTQ", "HNL", "NIO", "CRC", "PAB", "DOP", "SVC")
}

private fun getFormatLocale(currentLocale: Locale, currencyCode: String?): Locale {
    return if (isLatamStyle(currencyCode)) {
        // es-MX garantiza el símbolo $ y el formato base adecuado para la región
        if (currentLocale.country == "MX" || currentLocale.country == "US") currentLocale
        else Locale.forLanguageTag("es-MX")
    } else {
        currentLocale
    }
}

private fun applyLatamSymbols(df: DecimalFormat) {
    val symbols = df.decimalFormatSymbols
    symbols.groupingSeparator = ','
    symbols.decimalSeparator = '.'
    df.decimalFormatSymbols = symbols
}

/**
 * Formats a string representing a numeric value as a currency string.
 */
@Composable
@ReadOnlyComposable
fun String.formatAsCurrency(
    defaultLocale: Locale? = null,
    currencyCode: String? = LocalCurrencyCode.current,
    onErrorReturn: String = this
): String {
    val context = LocalContext.current
    val localeToUse = defaultLocale ?: context.getCurrentLocale()

    return try {
        Money.format(this, localeToUse, currencyCode)
    } catch (e: Exception) {
        onErrorReturn
    }
}

/**
 * Formats a Double value as a currency string.
 */
@Composable
@ReadOnlyComposable
fun Double.formatAsCurrency(
    defaultLocale: Locale? = null,
    currencyCode: String? = LocalCurrencyCode.current,
    onErrorReturn: String = "0.0"
): String {
    val context = LocalContext.current
    val localeToUse = defaultLocale ?: context.getCurrentLocale()

    return try {
        Money.format(this, localeToUse, currencyCode)
    } catch (e: Exception) {
        onErrorReturn
    }
}

@Composable
@ReadOnlyComposable
private fun Context.getCurrentLocale(): Locale {
    return this.resources.configuration.locales.get(0)
}

/**
 * A [VisualTransformation] that formats the input text as currency.
 */
fun CurrencyVisualTransformation(
    locale: Locale = Locale.getDefault(),
    currencyCode: String? = null,
    currency: Currency = currencyCode?.let { Currency.getInstance(it) } ?: Money.getCurrency(locale),
    fractionDigits: Int = 2,
    showZeroWhenEmpty: Boolean = false
): VisualTransformation {
    val formatLocale = getFormatLocale(locale, currency.currencyCode)

    val df = (NumberFormat.getCurrencyInstance(formatLocale) as DecimalFormat).apply {
        this.currency = currency
        
        if (isLatamStyle(currency.currencyCode)) {
            applyLatamSymbols(this)
        }

        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
        decimalFormatSymbols = decimalFormatSymbols.apply { currencySymbol = "" }
        positivePrefix = positivePrefix.trim().trim('\u00A0')
        positiveSuffix = positiveSuffix.trim().trim('\u00A0')
        negativePrefix = negativePrefix.trim().trim('\u00A0')
        negativeSuffix = negativeSuffix.trim().trim('\u00A0')
    }

    return VisualTransformation { text ->
        val raw = text.text.trim()

        val amount: BigDecimal = when {
            raw.isBlank() -> BigDecimal.ZERO
            // Si el usuario usa '.' o ',' tratamos como decimales reales (no centavos)
            raw.any { it == '.' || it == ',' } -> {
                // Conserva solo dígitos, '.', ',' y signo
                var s = raw.replace(Regex("[^0-9,.-]"), "")
                val lastDot = s.lastIndexOf('.')
                val lastComma = s.lastIndexOf(',')
                val decimalSep = when {
                    lastDot >= 0 && lastComma >= 0 -> if (lastDot > lastComma) '.' else ','
                    lastDot >= 0 -> '.'
                    lastComma >= 0 -> ','
                    else -> null
                }
                // Quita separadores de miles y normaliza a '.'
                if (decimalSep != null) {
                    s = if (decimalSep == '.') {
                        s.replace(",", "")
                    } else {
                        s.replace(".", "").replace(',', '.')
                    }
                }
                s.toBigDecimalOrNull() ?: BigDecimal.ZERO
            }
            else -> {
                // Sin separador: interpreta como centavos
                val digits = raw.filter(Char::isDigit)
                if (digits.isEmpty()) BigDecimal.ZERO
                else BigDecimal(digits).movePointLeft(fractionDigits)
            }
        }

        val formattedRaw = if (raw.isNotBlank() || showZeroWhenEmpty) df.format(amount) else ""
        val formatted = formattedRaw.trim().trim('\u00A0')

        val originalLen = raw.length
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = formatted.length
            override fun transformedToOriginal(offset: Int) = originalLen
        }

        TransformedText(AnnotatedString(formatted), mapping)
    }
}