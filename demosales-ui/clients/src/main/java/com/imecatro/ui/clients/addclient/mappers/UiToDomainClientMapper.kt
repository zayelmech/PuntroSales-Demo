package com.imecatro.ui.clients.addclient.mappers

import com.imecatro.domain.clients.model.AddressDomainModel
import com.imecatro.domain.clients.model.ClientDomainModel
import com.imecatro.ui.clients.addclient.model.AddressUiModel
import com.imecatro.ui.clients.addclient.model.ClientUiModel

fun AddressUiModel.mapToDomainModel(): AddressDomainModel {
    return AddressDomainModel(
        street = this.street,
        numberExt = this.numberExt,
        numberInt = this.numberInt,
        city = this.city,
        state = this.state
    )
}

fun ClientUiModel.mapToDomainClientModel(): ClientDomainModel {
    return ClientDomainModel(
        id = this.id,
        name = this.name,
        surName = this.surName,
        email = this.email,
        phone = this.phone,
        address = this.address.mapToDomainModel(),
        noPurchases = this.noPurchases
    )
}