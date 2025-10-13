package com.imecatro.products.ui.catalog.model

data class ProductCatalogModel(
    val name: String?,
    val unit: String? = null,
    val price: String? = null,        // already formatted, e.g. "$12.99"
    val imageUrl: String? = null,
    val category: String? = null
)
