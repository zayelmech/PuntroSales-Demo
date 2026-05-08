package com.imecatro.demosales.ui.clients.list.model

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri

class ClientUiModel(
    val id: Long?,
    val name: String?,
    val number: String?,
    val image: String?,
    val address: String?
) {



    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.NONE)
        fun getDummy(): ClientUiModel {
            return ClientUiModel(
                id = 1,
                name = "Juan Pérez - Abarrotes 'La Esquina'",
                number = "555-0123-456",
                image = "",
                address = "Av. Principal 123, Col. Centro"
            )
        }
    }

}


internal val ClientUiModel.imageUrl: Uri
    get() = (this.image ?: "").toUri()
