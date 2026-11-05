package com.imecatro.demosales.domain.products.model

/** Immutable query identity. ID remains the tie breaker for every ordering. */
data class ProductsQuery(
    val search: String = "",
    val categoryNames: Set<String> = emptySet(),
    val includeUncategorized: Boolean = false,
    val order: ProductOrder = ProductOrder.CREATED,
    val descending: Boolean = false,
)

enum class ProductOrder { NAME, PRICE, STOCK, CREATED }

/** Only the data used by an inventory row; no descriptions or stock history. */
data class ProductListItem(
    val id: Long,
    val name: String,
    val price: Double,
    val unit: String,
    val stock: Double,
    val imageUri: String,
    val category: String?,
)
