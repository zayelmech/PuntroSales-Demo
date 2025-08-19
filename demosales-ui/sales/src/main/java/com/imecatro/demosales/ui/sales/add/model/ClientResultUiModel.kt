package com.imecatro.demosales.ui.sales.add.model

import android.net.Uri

data class ClientResultUiModel(
    val id: Long,
    val name: String,
    val imageUri: Uri? = null
)
