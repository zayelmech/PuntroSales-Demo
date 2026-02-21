package com.imecatro.demosales.ui.clients.edit.mappers

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.edit.model.EditClientUiModel

internal fun EditClientUiModel.toDomain(): ClientDomainModel {
    return ClientDomainModel(
        id = this.id,
        name = this.clientName,
        phoneNumber = this.phoneNumber,
        latitude = this.latitude,
        longitude = this.longitude,
        shipping = this.clientAddress,
        avatarUri = this.imageUri?.toString() ?: ""
    )
}