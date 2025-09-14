package com.imecatro.demosales.ui.theme.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.util.Locale

internal object Money{
    fun format(double : String, locale: Locale = Locale("es", "MX")): String {

        val nf = NumberFormat.getCurrencyInstance(locale)
        return nf.format(double.toDouble())
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