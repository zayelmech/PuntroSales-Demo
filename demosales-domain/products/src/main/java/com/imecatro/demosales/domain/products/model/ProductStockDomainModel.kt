package com.imecatro.demosales.domain.products.model


data class ProductStockDomainModel(
    val quantity: Double,
    val cost: Double,
    val history: List<History>
) {
    data class History(
        val date: String,
        val qty: Double,
        val tittle: String
    )
}
