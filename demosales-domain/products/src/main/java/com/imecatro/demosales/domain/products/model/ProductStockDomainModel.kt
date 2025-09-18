package com.imecatro.demosales.domain.products.model


/**
 * Represents the stock information for a product.
 *
 * @property quantity The current quantity of the product in stock.
 * @property cost The cost of the product.
 * @property history A list of [History] objects representing the stock history of the product.
 */
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
