package com.imecatro.products.ui.update.model

import android.net.Uri

data class UpdateProductUiModel(
    val id : Long?,
    val name: String?,
    val price: String?,
    val currency: String?,
    val unit: String?,
    val stock : Double,
    val imageUri: Uri?,
    val details: String,
    val category: String?
)
