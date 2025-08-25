package com.imecatro.demosales.ui.theme.common

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * ISO-8601 UTC string → human-readable string (device locale & timezone).
 * Safe to call inside Text: Text(dateIso.localDate())
 */
@Composable
fun String.localDate(): String {
    val context = LocalContext.current
    val locale: Locale = Locale.getDefault()
    val is24h = DateFormat.is24HourFormat(context)
    val zone = ZoneId.systemDefault()

    // Build a locale-aware pattern that respects 12/24h
    val skeleton = if (is24h) "yMMMd Hm" else "yMMMd hm"
    val pattern = remember(locale, is24h) {
        DateFormat.getBestDateTimePattern(locale, skeleton)
    }

    return remember(this, pattern, zone, locale) {
        val instant = parseIsoToInstant(this)
        instant
            ?.atZone(zone)
            ?.format(DateTimeFormatter.ofPattern(pattern, locale))
            ?: this // fallback: return original if parsing fails
    }
}

// Robust ISO parser for common variants
private fun parseIsoToInstant(iso: String): Instant? {
    return try {
        Instant.parse(iso) // e.g., 2025-08-24T01:23:45Z
    } catch (_: Exception) {
        try {
            OffsetDateTime.parse(iso, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        } catch (_: Exception) {
            try {
                ZonedDateTime.parse(iso, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant()
            } catch (_: Exception) {
                null
            }
        }
    }
}
