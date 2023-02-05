package com.imecatro.demosales.domain.products.products.model


data class ProductDomainModel(
    var id: Int?,
    val name: String?,
    val price: Float?,
    val currency: String?,
    val unit: ProductUnit = ProductUnit.Default,
    val details: String,
    val imageUri : String?
)

