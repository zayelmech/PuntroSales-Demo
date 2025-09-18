package com.imecatro.demosales.domain.products.model

/**
 * Data class representing a product category in the domain layer.
 *
 * @property id The unique identifier of the product category. Can be null if the category is not yet persisted.
 * @property name The name of the product category.
 */
data class ProductCategoryDomainModel(
    val id: Long? = 0,
    val name: String
)
