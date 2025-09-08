package com.imecatro.demosales.ui.sales.add.model

import android.net.Uri

/**
 *
 *  @param id is the id of the client
 *  @param name is the name of the client
 *  @param address is the address of the client
 *  @param imageUri is the image of the client
 */
data class ClientResultUiModel(
    val id: Long,
    val name: String,
    val address: String,
    val imageUri: Uri? = null
)
