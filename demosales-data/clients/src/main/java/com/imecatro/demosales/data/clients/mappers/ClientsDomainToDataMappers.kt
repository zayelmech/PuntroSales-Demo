package com.imecatro.demosales.data.clients.mappers

import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import com.imecatro.demosales.domain.clients.model.ClientDomainModel

fun ClientDomainModel.toData(version: Int): ClientRoomEntity {
    val timestamp = System.currentTimeMillis()
    return ClientRoomEntity(
        id = id,
        name = this.name,
        phone = this.phoneNumber,
        address = this.shipping,
        imageUri = avatarUri ?: "",
        timestamp = timestamp,
        tableVersion = version.toString(),
        latitude = this.latitude,
        longitude = this.longitude
    )
}
