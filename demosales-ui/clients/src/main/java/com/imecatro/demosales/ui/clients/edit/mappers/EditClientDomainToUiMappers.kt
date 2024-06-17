package com.imecatro.demosales.ui.clients.edit.mappers

import android.net.Uri
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.edit.model.EditClientUiModel

internal fun ClientDomainModel.toUi(current: EditClientUiModel): EditClientUiModel {
    return current.copy(
        id = this.id,
        clientName = this.name,
        phoneNumber = this.phoneNumber,
        clientAddress = this.shipping,
        imageUri = Uri.parse(this.avatarUri?:"")
    )
}
