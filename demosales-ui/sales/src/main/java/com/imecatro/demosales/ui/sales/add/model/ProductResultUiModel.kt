package com.imecatro.demosales.ui.sales.add.model

import android.net.Uri

data class ProductResultUiModel(
    val id: Int = 0,
    val name: String = "",
    val price: Float = 0f,
    val imageUri: Uri? = null
)
