package com.imecatro.demosales.domain.sales.list.model

import com.imecatro.demosales.domain.sales.model.OrderStatus

data class SaleOnListDomainModel(
    val id: Long,
    val clientName: String,
    var date: Long, //date when ticket was created
    var total: Double,
    val status: OrderStatus
)