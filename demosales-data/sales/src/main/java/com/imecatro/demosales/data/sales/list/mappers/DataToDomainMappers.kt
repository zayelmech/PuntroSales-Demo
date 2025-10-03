package com.imecatro.demosales.data.sales.list.mappers

import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel


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


internal fun SaleDataRoomModel.toSaleDomain(): SaleDomainModel {
    return SaleDomainModel(
        id = id,
        clientId = clientId?:0L,
        clientName = clientSnapshot?.name?:"",
        clientAddress = clientSnapshot?.address?:"",
        date = creationDateMillis.toString(),
        productsList = emptyList(),
        totals = SaleDomainModel.Costs(
            extraCost = totals?.extra?:0.0,
            discount = totals?.discount?:0.0,
            subTotal = totals?.subtotal?:0.0,
            total = totals?.total?:0.0
        ),
        status = status.toOrderStatus(),
        note = note
    )
}
