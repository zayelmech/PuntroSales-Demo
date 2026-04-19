package com.imecatro.demosales.ui.clients.details.mappers

import androidx.core.net.toUri
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import java.util.Locale

internal fun ClientDomainModel.toUi(): ClientDetailsUiModel {

    return ClientDetailsUiModel(
        clientId = this.id,
        clientName = this.name,
        phoneNumber = this.phoneNumber,
        clientAddress = this.shipping,
        latitude = this.latitude ?: 0.0,
        longitude = this.longitude ?: 0.0,
        imageUri = (this.avatarUri ?: "").toUri(),
        accumulatedPurchases = String.format(Locale.getDefault(), "$%.2f", this.accumulatedPurchases),
        isFavorite = this.isFavorite
    )
}
