package com.imecatro.demosales.domain.sales.details

import com.imecatro.demosales.domain.sales.model.Order

data class SaleDetailsDomainModel(
    val list: List<Order>,
    val clientName: String = "Guest",
    val note: String = "" ,
    val shippingCost: Double = 0.0,
    val tax: Double = 0.0,
    val extra: Double = 0.0,
    val total: Double = 0.0,
)