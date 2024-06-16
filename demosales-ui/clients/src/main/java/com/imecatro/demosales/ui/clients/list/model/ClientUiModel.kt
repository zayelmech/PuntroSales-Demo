package com.imecatro.demosales.ui.clients.list.model

import android.net.Uri
import androidx.annotation.VisibleForTesting

class ClientUiModel(
    val id: Int?,
    val name: String?,
    val number: String?,
    val image: String?,
    val address: String?
) {



    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.NONE)
        fun getDummy(): ClientUiModel {
            return ClientUiModel(id = 0, name = "Abdiel C. Rojas", number = "123456", image = "", address = "Calle 123")
        }
    }

}


internal val ClientUiModel.imageUrl: Uri
    get() = Uri.parse(this.image)
