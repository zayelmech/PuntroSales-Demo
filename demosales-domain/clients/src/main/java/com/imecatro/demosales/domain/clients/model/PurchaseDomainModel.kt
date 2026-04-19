package com.imecatro.demosales.domain.clients.model

data class PurchaseDomainModel(
    val id: Long = 0,
    val purchaseNumber: String,
    val clientId: Long,
    val description: String,
    val amount: Double,
    val date: Long
)