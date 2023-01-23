package com.imecatro.ui.add.model

import android.net.Uri

data class AddProductUiModel(
    val name: String?,
    val price: String?,
    val currency: String?,
    val unit: String?,
    val imageUri: Uri?,
    val details: String
)
