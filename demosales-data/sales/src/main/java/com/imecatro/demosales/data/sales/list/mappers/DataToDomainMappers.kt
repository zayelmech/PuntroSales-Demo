package com.imecatro.demosales.data.sales.list.mappers

import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus


internal fun SaleDataRoomModel.toDomain(
    total: Double,
    clientName: String = "Guest"
): SaleOnListDomainModel =
    SaleOnListDomainModel(
        id = id,
        clientName = clientName, //TODO add functionality
        date = creationDateMillis, //TODO add functionality
        total = total,
        status = status.toOrderStatus()
    )


internal fun String.toOrderStatus(): OrderStatus = OrderStatus.values().find { it.str == this }
    ?: OrderStatus.PENDING

