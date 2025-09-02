package com.imecatro.demosales.data.sales.list.mappers

import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus


internal fun String.toOrderStatus(): OrderStatus = OrderStatus.entries.find { it.str == this }
    ?: OrderStatus.INITIALIZED


internal fun SaleDataRoomModel.toDomain(): SaleOnListDomainModel {
    return SaleOnListDomainModel(
        id = id,
        clientName = clientSnapshot?.name?:"",
        date = creationDateMillis,
        total = totals?.total?:0.0,
        status = status.toOrderStatus()
    )
}
