package com.imecatro.products.data.model

import com.imecatro.demosales.domain.products.model.ProductListItem

data class ProductListRow(
    val id: Long,
    val name: String,
    val nameKey: String,
    val price: Double,
    val unit: String,
    val stock: Double,
    val imageUri: String,
    val category: String?,
) {
    fun toListItem() = ProductListItem(id, name, price, unit, stock, imageUri, category)
}
