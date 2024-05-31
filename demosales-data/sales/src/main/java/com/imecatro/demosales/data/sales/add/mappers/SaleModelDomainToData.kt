package com.imecatro.demosales.data.sales.add.mappers

import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleDomainModel


fun SaleDomainModel.toData(id: Long): SaleDataRoomModel {

    return SaleDataRoomModel(
        id = id,
        clientId = this.clientId,
        creationDateMillis = this.date.toLong(),
        status = this.status.str,
    )
}

internal fun Order.toDataSource(saleId: Long): OrderDataRoomModel =
    OrderDataRoomModel(
        id=id,
        saleId = saleId,
        productId = productId,
        productName = productName,
        productPrice = productPrice,
        qty = qty,
        productImage = ""
    )
