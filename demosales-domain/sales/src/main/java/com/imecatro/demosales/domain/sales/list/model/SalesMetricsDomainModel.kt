package com.imecatro.demosales.domain.sales.list.model

import java.util.Date


/**
 * @today today's sales
 * @weekly weekly sales
 * @monthly monthly sales
 */
data class SalesMetricsDomainModel(
    val today: List<GroupedSalesByHour>,
    val weekly: List<GroupedSalesByDay>,
    val monthly: List<GroupedSalesByDay>
)


/**
 * @param date date of the day
 * @param total sum of totals of all sales on that day, total param is in [SaleOnListDomainModel]
 * @param sales list of sales on that day
 */
data class GroupedSalesByDay(
    val date: Date,
    val total: Double,
    val sales: List<SaleOnListDomainModel>,
)



data class GroupedSalesByHour(
    val hour: Int, // 0..23
    val date: Date,
    val total: Double,
    val sales: List<SaleOnListDomainModel>,
)