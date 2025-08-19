package com.imecatro.demosales.data.sales.model

data class SaleFullTransactionModel(
    val id: Long,
    val clientName: String,
    val date: Long,        // maps from s.creationDateMillis
    val total: Double,     // sum(order) + extra
    val status: String
)