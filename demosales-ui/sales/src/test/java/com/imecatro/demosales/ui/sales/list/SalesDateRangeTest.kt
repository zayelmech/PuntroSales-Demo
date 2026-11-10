package com.imecatro.demosales.ui.sales.list

import com.imecatro.demosales.ui.sales.list.model.SalesDateRange
import com.imecatro.demosales.ui.sales.list.model.toPickerDate
import com.imecatro.demosales.ui.sales.list.model.toPickerMillis
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.*
import org.junit.Test

class SalesDateRangeTest {
    private val zone = ZoneId.of("America/Monterrey")
    private val day = LocalDate.of(2026, 11, 10)

    @Test fun `single date includes entire local day and excludes adjacent days`() {
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val nextDay = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val range = SalesDateRange(day)
        assertFalse(range.contains(start - 1, zone))
        assertTrue(range.contains(start, zone))
        assertTrue(range.contains(nextDay - 1, zone))
        assertFalse(range.contains(nextDay, zone))
    }

    @Test fun `custom range includes both endpoints and interior dates`() {
        val range = SalesDateRange(day, day.plusDays(2))
        for (offset in 0L..2L) {
            assertTrue(range.contains(day.plusDays(offset).atTime(23, 59).atZone(zone).toInstant().toEpochMilli(), zone))
        }
        assertFalse(range.contains(day.plusDays(3).atStartOfDay(zone).toInstant().toEpochMilli(), zone))
    }

    @Test fun `UTC instant is filtered by local calendar date`() {
        val instant = Instant.parse("2026-11-11T02:00:00Z").toEpochMilli()
        assertTrue(SalesDateRange(day).contains(instant, zone))
        assertFalse(SalesDateRange(day.plusDays(1)).contains(instant, zone))
    }

    @Test fun `day boundaries account for daylight saving changes`() {
        val dstZone = ZoneId.of("America/New_York")
        for (date in listOf(LocalDate.of(2026, 3, 8), LocalDate.of(2026, 11, 1))) {
            val end = date.plusDays(1).atStartOfDay(dstZone).toInstant().toEpochMilli()
            assertTrue(SalesDateRange(date).contains(end - 1, dstZone))
            assertFalse(SalesDateRange(date).contains(end, dstZone))
        }
    }

    @Test fun `picker values preserve calendar date independently of local zone`() {
        assertEquals(Instant.parse("2026-11-10T00:00:00Z").toEpochMilli(), day.toPickerMillis())
        assertEquals(day, day.toPickerMillis().toPickerDate())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reversed range is rejected`() {
        SalesDateRange(day, day.minusDays(1))
    }
}
