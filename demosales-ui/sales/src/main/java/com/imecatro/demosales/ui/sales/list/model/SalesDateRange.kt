package com.imecatro.demosales.ui.sales.list.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/** Inclusive calendar dates in the device zone; null in UI state means all dates. */
data class SalesDateRange(val start: LocalDate, val end: LocalDate = start) {
    init {
        require(!end.isBefore(start)) { "End date must be on or after start date" }
    }

    fun contains(timestampMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Boolean {
        val date = Instant.ofEpochMilli(timestampMillis).atZone(zone).toLocalDate()
        return !date.isBefore(start) && !date.isAfter(end)
    }
}

// Material date pickers encode calendar dates at midnight UTC, independent of device zone.
internal fun LocalDate.toPickerMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

internal fun Long.toPickerDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
