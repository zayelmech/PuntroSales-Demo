package com.imecatro.products.ui.update.model

import android.net.Uri

data class UpdateProductUiModel(
    val id: Long? = null,
    val name: String = "",
    val price: String = "",
    val currency: String = "",
    val unit: String = "",
    val stock: Double  = 0.0,
    val imageUri: Uri? = null,
    val details: String = "",
    val category: String? = null,
    val barcode: String? = null
)