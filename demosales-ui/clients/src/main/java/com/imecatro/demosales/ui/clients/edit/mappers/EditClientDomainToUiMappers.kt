package com.imecatro.demosales.ui.clients.edit.mappers

import android.net.Uri
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.edit.model.EditClientUiModel
import androidx.core.net.toUri

internal fun ClientDomainModel.toUi(current: EditClientUiModel): EditClientUiModel {
    return current.copy(
        id = this.id,
        clientName = this.name,
        phoneNumber = this.phoneNumber,
        clientAddress = this.shipping,
        imageUri = this.avatarUri ?.toUri()
    )
}
