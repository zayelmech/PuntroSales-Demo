package com.imecatro.demosales.data.sales.add.mappers

import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.model.SaleDomainModel


fun SaleDomainModel.toData(): SaleDataRoomModel {

    return SaleDataRoomModel(
        id = 0,
        clientId = this.clientId,
        creationDateMillis = this.date.toLong(),
        status = this.status.str,
    )
}
