package com.imecatro.demosales.domain.sales.list.usecases

import com.imecatro.demosales.domain.sales.list.model.GroupedSalesByDay
import com.imecatro.demosales.domain.sales.list.model.GroupedSalesByHour
import com.imecatro.demosales.domain.sales.list.model.SalesMetricsDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Date

class GetSalesMetricsUseCase(private val allSalesRepository: AllSalesRepository) {


    operator fun invoke(
        referenceDate: LocalDate = LocalDate.now()
    ): Flow<SalesMetricsDomainModel> {
        return allSalesRepository.getAllSales().map { allSales ->
            val zoneId = ZoneId.systemDefault()

            val completedSales = allSales.filter { it.status == OrderStatus.COMPLETED }

            val salesWithDateTime = completedSales.map { sale ->
                sale to Instant.ofEpochMilli(sale.date)
                    .atZone(zoneId)
                    .toLocalDateTime()
            }

            val today = salesWithDateTime
                .filter { (_, dateTime) -> dateTime.toLocalDate() == referenceDate }
                .groupBy { (_, dateTime) -> dateTime.hour }
                .map { (hour, salesInHour) ->
                    GroupedSalesByHour(
                        hour = hour,
                        date = Date.from(
                            referenceDate
                                .atStartOfDay(zoneId)
                                .plusHours(hour.toLong())
                                .toInstant()
                        ),
                        total = salesInHour.sumOf { (sale, _) -> sale.total },
                        sales = salesInHour.map { (sale, _) -> sale }
                    )
                }
                .sortedBy { it.hour }

            val groupedByDay = salesWithDateTime
                .groupBy { (_, dateTime) -> dateTime.toLocalDate() }
                .mapValues { (localDate, salesInDay) ->
                    GroupedSalesByDay(
                        date = Date.from(localDate.atStartOfDay(zoneId).toInstant()),
                        total = salesInDay.sumOf { (sale, _) -> sale.total },
                        sales = salesInDay.map { (sale, _) -> sale }
                    )
                }

            val startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val currentYearMonth = YearMonth.from(referenceDate)
            val startOfMonth = currentYearMonth.atDay(1)
            val endOfMonth = currentYearMonth.atEndOfMonth()

            SalesMetricsDomainModel(
                today = today,
                weekly = groupedByDay
                    .filterKeys { date -> date in startOfWeek..endOfWeek }
                    .toSortedMap()
                    .values
                    .toList(),
                monthly = groupedByDay
                    .filterKeys { date -> date in startOfMonth..endOfMonth }
                    .toSortedMap()
                    .values
                    .toList()
            )
        }
    }
}
