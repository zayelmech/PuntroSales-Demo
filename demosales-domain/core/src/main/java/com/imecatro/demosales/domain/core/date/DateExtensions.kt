package com.imecatro.demosales.domain.core.date

import java.time.Instant
import java.time.format.DateTimeFormatter

fun Long.convertMillisToDate(): String {
    val instant = Instant.ofEpochMilli(this)
    // DateTimeFormatter.ISO_INSTANT correctly formats an Instant
    // to the full ISO 8601 format in UTC, ending with 'Z'.
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}