package com.imecatro.demosales.data.sales.list.mappers

import com.imecatro.demosales.data.sales.model.SaleFullTransactionModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus


internal fun SaleFullTransactionModel.toDomain() : SaleOnListDomainModel{
    return SaleOnListDomainModel(
        id = id,
        clientName = clientName, //TODO add functionality
        date = date, //TODO add functionality
        total = total,
        status = status.toOrderStatus()
    )
}
internal fun String.toOrderStatus(): OrderStatus = OrderStatus.entries.find { it.str == this }
    ?: OrderStatus.INITIALIZED

