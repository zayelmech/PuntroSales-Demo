package com.imecatro.demosales.ui.clients.edit.model

import android.net.Uri

data class EditClientUiModel(
    val id: Int,
    val clientName: String = "",
    val phoneNumber: String = "",
    val clientAddress: String = "",
    val imageUri: Uri? = null,

    //Presenter
    val isFetchingClientDetails: Boolean = false,
    val error: String? = null,
    val isEditingClient: Boolean = false,
    val isClientEdited: Boolean = false,
)

internal val EditClientUiModel.isLoading: Boolean
    get() = isFetchingClientDetails || isEditingClient

internal val EditClientUiModel.isFormValid: Boolean
    get() =
        clientName.isNotBlank() && phoneNumber.isNotBlank()