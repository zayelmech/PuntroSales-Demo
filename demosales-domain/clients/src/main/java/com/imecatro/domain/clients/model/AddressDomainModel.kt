package com.imecatro.domain.clients.model

data class AddressDomainModel(
    val street : String,
    val numberExt: String,
    val numberInt: String?,
    val city: String,
    val state: String
)
