package com.imecatro.demosales.ui.clients.list.mappers

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel

internal fun List<ClientDomainModel>.toUiModel(): List<ClientUiModel> {
    return map { it.toUiModel() }
}

internal fun ClientDomainModel.toUiModel(): ClientUiModel {
    return ClientUiModel(
        id = this.id,
        name = this.name,
        number = this.phoneNumber,
        image = this.avatarUri,
        address = this.shipping
    )
}

internal fun ClientUiModel.toDomain(): ClientDomainModel {
    return ClientDomainModel(
        id = this.id ?: 0,
        name = this.name ?: "",
        phoneNumber = this.number ?: "",
        avatarUri = this.image,
        shipping = this.address ?: "",
        accumulatedPurchases = 0.0, // Default or not used here
        isFavorite = false // Default or not used here
    )
}