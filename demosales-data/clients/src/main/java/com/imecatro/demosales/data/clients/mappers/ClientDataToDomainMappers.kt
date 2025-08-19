package com.imecatro.demosales.data.clients.mappers

import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import com.imecatro.demosales.domain.clients.model.ClientDomainModel


internal fun ClientRoomEntity.toDomain(): ClientDomainModel {
    return ClientDomainModel(
        id = id,
        name = name,
        phoneNumber = this.phone?:"",
        shipping = this.address?:"",
        avatarUri = this.imageUri?.ifEmpty { null }
    )
}