package com.imecatro.demosales.ui.theme.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Money{
    fun format(double : String, locale: Locale = Locale("es", "MX")): String {

        val nf = NumberFormat.getCurrencyInstance(locale)
        return nf.format(double.toDouble())
    }

    fun format(double : Double, locale: Locale = Locale("es", "MX")): String {

        val nf = NumberFormat.getCurrencyInstance(locale)
        return nf.format(double)
    }

    fun toDouble(
        formatted: String,
        locale: Locale = Locale.getDefault(),
        currency: Currency = Currency.getInstance(locale),
        fractionDigits: Int = currency.defaultFractionDigits.let { if (it < 0) 2 else it }
    ): Double {
        if (formatted.isBlank()) return 0.0
        val digits = formatted.filter(Char::isDigit)
        if (digits.isEmpty()) return 0.0
        return BigDecimal(digits)
            .movePointLeft(fractionDigits) // "5500" -> 55.00 si fractionDigits=2
            .toDouble()
    }
}

/**
 * Formatea una cadena que representa un valor numérico como una cadena de moneda
 * utilizando el Locale primario del contexto actual o un Locale específico.
 *
 * @param defaultLocale El Locale a usar si no se desea el del contexto. Si es null, se usa el del contexto.
 * @param onErrorReturn El valor a devolver si la cadena no puede ser formateada como moneda.
 * @return La cadena formateada como moneda o el valor de `onErrorReturn`.
 */
@Composable
@ReadOnlyComposable // Indica que la función solo lee el estado de Compose (LocalContext)
fun String.formatAsCurrency(
    defaultLocale: Locale? = null,
    onErrorReturn: String = this // Devuelve la cadena original en caso de error por defecto
): String {
    val context = LocalContext.current
    val localeToUse = defaultLocale ?: context.getCurrentLocale()

    // Si tu Money.format ya maneja el String, puedes llamarlo directamente.
    // Si no, necesitarías parsear 'this' (el String) a un número (Double, BigDecimal) primero.
    // Asumiendo que Money.format puede manejar un String que es un número:
    return try {
        Money.format(this, localeToUse)
        // O si Money.format espera un número y no un String:
        // val numericValue = this.toBigDecimalOrNull()
        // if (numericValue != null) {
        //     Money.format(numericValue, localeToUse) // Necesitarías un Money.format(BigDecimal, Locale)
        // } else {
        //     onErrorReturn
        // }
    } catch (e: Exception) {
        // Captura genérica por si Money.format lanza alguna otra excepción no esperada.
        // Podrías ser más específico con los tipos de excepción.
        onErrorReturn
    }
}

/**
 * Formatea una cadena que representa un valor numérico como una cadena de moneda
 * utilizando el Locale primario del contexto actual o un Locale específico.
 *
 * @param defaultLocale El Locale a usar si no se desea el del contexto. Si es null, se usa el del contexto.
 * @param onErrorReturn El valor a devolver si la cadena no puede ser formateada como moneda.
 * @return La cadena formateada como moneda o el valor de `onErrorReturn`.
 */
@Composable
@ReadOnlyComposable // Indica que la función solo lee el estado de Compose (LocalContext)
fun Double.formatAsCurrency(
    defaultLocale: Locale? = null,
    onErrorReturn: String = "0.0" // Devuelve la cadena original en caso de error por defecto
): String {
    val context = LocalContext.current
    val localeToUse = defaultLocale ?: context.getCurrentLocale()

    // Si tu Money.format ya maneja el String, puedes llamarlo directamente.
    // Si no, necesitarías parsear 'this' (el String) a un número (Double, BigDecimal) primero.
    // Asumiendo que Money.format puede manejar un String que es un número:
    return try {
        Money.format(this, localeToUse)
        // O si Money.format espera un número y no un String:
        // val numericValue = this.toBigDecimalOrNull()
        // if (numericValue != null) {
        //     Money.format(numericValue, localeToUse) // Necesitarías un Money.format(BigDecimal, Locale)
        // } else {
        //     onErrorReturn
        // }
    } catch (e: Exception) {
        // Captura genérica por si Money.format lanza alguna otra excepción no esperada.
        // Podrías ser más específico con los tipos de excepción.
        onErrorReturn
    }
}

/**
 * Helper para obtener el Locale primario de la configuración del contexto.
 */
@Composable
@ReadOnlyComposable
private fun Context.getCurrentLocale(): Locale {
    return this.resources.configuration.locales.get(0)
}

// ------ Ejemplo de Uso ------
// @Composable
// fun MyComposable() {
//     val priceString = "1234.56"
//     val unformattableString = "abc"
//
//     Text(text = "Precio (actual): ${priceString.formatAsCurrency()}")
//     Text(text = "Precio (USD): ${priceString.formatAsCurrency(Locale.US)}")
//     Text(text = "Inválido: ${unformattableString.formatAsCurrency(onErrorReturn = "No disponible")}")
//
//     val anotherPrice = "99.9"
//     Text(text = "Otro Precio: ${anotherPrice.formatAsCurrency(defaultLocale = Locale("es", "MX"))}")
// }



/**
 * A [VisualTransformation] that formats the input text as currency.
 *
 * This transformation will:
 * - Filter out non-digit characters from the input.
 * - Interpret the remaining digits as the minor currency unit (e.g., cents for USD).
 * - Format the resulting numeric value as a currency string according to the provided
 *   `locale`, `currency`, and `fractionDigits`.
 * - Optionally display "0" formatted as currency if the input is empty and
 *   `showZeroWhenEmpty` is true.
 *
 * The [OffsetMapping] is simplified: it always maps the original cursor position to the
 * end of the transformed text, and the transformed cursor position to the end of the
 * original text. This is a common approach for input fields where the formatting
 * can significantly change the text length and structure.
 *
 * @param locale The [Locale] to use for formatting the currency. Defaults to the system's
 *   default locale.
 * @param currency The [Currency] to use for formatting. Defaults to the currency
 *   associated with the `locale`.
 * @param fractionDigits The number of fraction digits to display. Defaults to the
 *   currency's default number of fraction digits. If the currency's default is unknown (-1),
 *   it falls back to 2.
 * @param showZeroWhenEmpty If `true`, displays the formatted currency representation of zero
 *   (e.g., "$0.00") when the input text is empty. If `false`, displays an empty string.
 *   Defaults to `false`.
 * @return A [VisualTransformation] that applies currency formatting.
 */

fun CurrencyVisualTransformation(
    locale: Locale = Locale.getDefault(),
    fractionDigits: Int = 2,            // ajusta según tu moneda si quieres
    showZeroWhenEmpty: Boolean = false
): VisualTransformation {
    val df = (NumberFormat.getCurrencyInstance(locale) as DecimalFormat).apply {
        this.currency = currency
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
        // Ocultar símbolo de moneda
        decimalFormatSymbols = decimalFormatSymbols.apply { currencySymbol = "" }
        // Limpia espacios duros u otros restos alrededor
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