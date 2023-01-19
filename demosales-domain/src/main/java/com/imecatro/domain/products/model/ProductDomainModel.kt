package com.imecatro.domain.products.model

data class ProductDomainModel(
    val id: Int?,
    val name: String?,
    val price: Float?,
    val unit: ProductUnit = ProductUnit.DEFAULT
)

