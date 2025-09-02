package com.imecatro.demosales.domain.sales.details

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus

data class SaleDetailsDomainModel(
    val list: List<Order>,
    val clientId: Long = 0,
    val note: String = "",
    val discount : Double = 0.0,
    val extra: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.INITIALIZED
)