package com.imecatro.demosales.ui.clients.list.mappers

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel

internal fun List<ClientDomainModel>.toUiModel(): List<ClientUiModel> {
    return map {
        ClientUiModel(
            id = it.id,
            name = it.name,
            number = it.phoneNumber,
            image = it.avatarUri,
            address = it.shipping
        )
    }
}