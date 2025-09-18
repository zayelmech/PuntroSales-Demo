package com.imecatro.demosales.domain.products.model

import com.imecatro.demosales.domain.core.model.ProductUnit


/**
 * Represents a product in the domain layer.
 *
 * @property id The unique identifier of the product. Can be null if the product hasn't been persisted.
 * @property name The name of the product. Can be null.
 * @property price The price of the product. Can be null.
 * @property currency The currency in which the price is expressed. Can be null.
 * @property unit The unit of measurement for the product (e.g., "kg", "pcs"). Defaults to [ProductUnit.Default.symbol].
 * @property stock Information about the product's stock.
 * @property details A detailed description of the product.
 * @property imageUri The URI of the product's image. Can be null if no image is available.
 * @property category The category to which the product belongs. Can be null if the product is not categorized.
 */
data class ProductDomainModel(
    var id: Long?,
    val name: String?,
    val price: Double?,
    val currency: String?,
    val unit: String? = ProductUnit.Default.symbol,
    val stock: ProductStockDomainModel,
    val details: String,
    val imageUri: String?,
    val category: ProductCategoryDomainModel?
)

