package com.imecatro.products.ui.list.model

import android.net.Uri

data class ProductUiModel(
    val id: Int?,
    val name: String?,
    val price: String?,
    val unit: String?,
    val stock : String,
    val imageUrl: Uri?,
)
