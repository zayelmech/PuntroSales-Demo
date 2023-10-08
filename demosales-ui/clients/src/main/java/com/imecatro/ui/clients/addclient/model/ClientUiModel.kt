package com.imecatro.ui.clients.addclient.model

data class ClientUiModel(
    val id: Int = 0,
    val name: String = "",
    val surName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: AddressUiModel = AddressUiModel(),
    val noPurchases: Int = 0
)