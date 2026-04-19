package com.imecatro.demosales.data.clients.mappers

import com.imecatro.demosales.data.clients.model.PurchaseRoomEntity
import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel

internal fun PurchaseDomainModel.toData(): PurchaseRoomEntity {
    return PurchaseRoomEntity(
        id = this.id,
        purchaseNumber = this.purchaseNumber,
        clientId = this.clientId,
        description = this.description,
        amount = this.amount,
        date = this.date
    )
}
