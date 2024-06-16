package com.imecatro.demosales.ui.clients.add.mappers

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.add.model.AddClientUiModel


fun AddClientUiModel.toDomain(): ClientDomainModel {
    return ClientDomainModel(
        id = 0,
        name = clientName,
        phoneNumber = phoneNumber,
        shipping = clientAddress,
        avatarUri = imageUri?.toString() ?: ""
    )

}