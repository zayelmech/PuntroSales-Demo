package com.imecatro.products.ui.update.model

import android.net.Uri

data class UpdateProductUiModel(
    val id : Int?,
    val name: String?,
    val price: String?,
    val currency: String?,
    val unit: String?,
    val imageUri: Uri?,
    val details: String
)
