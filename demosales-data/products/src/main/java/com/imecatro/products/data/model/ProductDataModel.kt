package com.imecatro.products.data.model

data class ProductDataModel(
    var id: Int,
    val name: String,
    val price: Float,
    val currency: String,
    val unit: String,
    val details: String,
    val imageUri: String
)