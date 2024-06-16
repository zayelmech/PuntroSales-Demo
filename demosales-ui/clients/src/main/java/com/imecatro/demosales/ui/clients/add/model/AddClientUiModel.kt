package com.imecatro.demosales.ui.clients.add.model

import android.net.Uri


data class AddClientUiModel(
    var clientName: String = "",
    var phoneNumber: String = "",
    var clientAddress: String = "",
    val imageUri: Uri? = null,

    //Presenter variables
    val isSavingClient: Boolean = false,
    val isClientSaved: Boolean = false,
    val error: String? = null
)


val AddClientUiModel.isLoading get() = isSavingClient

val AddClientUiModel.isSaved get() = isClientSaved


/**
 * Client Validation rules
 *
 * We cannot save a client without name and phone number
 *
 */
val AddClientUiModel.isFormValid get() = (clientName.isNotBlank() && phoneNumber.isNotBlank())