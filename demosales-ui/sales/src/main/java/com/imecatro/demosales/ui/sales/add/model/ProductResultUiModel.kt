package com.imecatro.demosales.ui.sales.add.model

import android.net.Uri

data class ProductResultUiModel(
    val id: Long = 0,
    val name: String = "",
    val price: Double = 0.0,
    val imageUri: Uri? = null,
    val stock: Double = 0.0,
    val qty: Double = 0.0
)
