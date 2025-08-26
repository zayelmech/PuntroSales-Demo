package com.imecatro.demosales.ui.clients.details.mappers

import android.net.Uri
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import androidx.core.net.toUri

internal fun ClientDomainModel.toUi(current: ClientDetailsUiModel): ClientDetailsUiModel {
    return current.copy(
        clientId = this.id,
        clientName = this.name,
        phoneNumber = this.phoneNumber,
        clientAddress = this.shipping,
        imageUri = (this.avatarUri ?: "").toUri()
    )
}
