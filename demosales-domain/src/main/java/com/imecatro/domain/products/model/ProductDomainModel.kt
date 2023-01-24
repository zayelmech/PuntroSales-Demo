package com.imecatro.domain.products.model


data class ProductDomainModel(
    val id: Int?,
    val name: String?,
    val price: Float?,
    val currency: String?,
    val unit: ProductUnit = ProductUnit.valueOf("pz"),
    val details: String,
    val imageUri : String?
)

