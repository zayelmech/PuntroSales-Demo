package com.imecatro.demosales.ui.clients.details.model

import android.net.Uri

data class ClientDetailsUiModel(
    val clientId: Long = 0,
    val clientName: String = "",
    val phoneNumber: String = "",
    val clientAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUri: Uri? = null,

    // Presenter parameters
    val isFetchingClientDetails: Boolean = false,
    val error: String? = null,
    val isDeletingClient: Boolean = false,
    val isClientDeleted: Boolean = false
) {
    companion object {
        internal val dummy =
            ClientDetailsUiModel(
                clientName = "Client Name",
                phoneNumber = "000 000 00 00",
                clientAddress = "1750 Broadway Avenue",
                imageUri = null
            )
    }
}


val ClientDetailsUiModel.isLoading get() = isFetchingClientDetails

