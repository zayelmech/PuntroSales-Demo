package com.imecatro.demosales.ui.clients.details.model

data class PurchaseUiModel(
    val id: Long,
    val purchaseNumber: String,
    val description: String,
    val amount: Double,
    val date: String
)
