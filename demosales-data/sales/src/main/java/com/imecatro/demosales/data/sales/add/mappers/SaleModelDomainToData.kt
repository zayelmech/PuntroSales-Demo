package com.imecatro.demosales.data.sales.add.mappers

import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.model.SaleModelDomain


fun SaleModelDomain.toData(): SaleDataRoomModel {

    return SaleDataRoomModel(
        id = 0,
        clientId = this.clientId,
        creationDateMillis = this.date,
        status = this.status.str,
    )
}
