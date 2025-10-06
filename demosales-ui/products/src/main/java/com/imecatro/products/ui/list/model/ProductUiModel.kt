package com.imecatro.products.ui.list.model

import android.net.Uri

data class ProductUiModel(
    val id: Long?,
    val name: String?,
    val price: String?,
    val unit: String?,
    val stock : String,
    val imageUrl: Uri?,
    val category: String?,
    val isSelected: Boolean = false

) {

    constructor(name: String) : this(1L, name, "10.0", "pz", "", null, null)
}
