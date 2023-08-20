package com.imecatro.domain.clients.model

data class ClientDomainModel(
    val id: Int,
    val name: String,
    val surName: String,
    val email: String,
    val phone: String,
    val address: AddressDomainModel,
    val noPurchases: Int
)