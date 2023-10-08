package com.imecatro.ui.clients.addclient.model

data class AddressUiModel(
    val street: String = "",
    val numberExt: String = "",
    val numberInt: String? = "",
    val city: String = "",
    val state: String = ""
)
