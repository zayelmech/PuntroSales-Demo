package com.imecatro.demosales.ui.clients.details.model

import android.net.Uri
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.UiState

data class ClientDetailsUiModel(
    val clientId: Long = 0,
    val clientName: String = "",
    val phoneNumber: String = "",
    val clientAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUri: Uri? = null,
    val purchases: List<PurchaseUiModel> = emptyList(),
    val accumulatedPurchases: String = "$0.00",
    val isFavorite: Boolean = false,

    // Presenter parameters
    val isFetchingClientDetails: Boolean = false,
    val error: String? = null,
    val isDeletingClient: Boolean = false,
    val isClientDeleted: Boolean = false,
    val isTogglingFavorite: Boolean = false
) : UiState {

    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingClientDetails || isDeletingClient || isTogglingFavorite
    }

    override fun getError(): ErrorUiModel? {
        return null
    }

    companion object {
        internal val dummy =
            ClientDetailsUiModel(
                clientName = "Client Name",
                phoneNumber = "000 000 00 00",
                clientAddress = "1750 Broadway Avenue",
            )

        internal val idle =
            ClientDetailsUiModel(
                clientName = "-",
                phoneNumber = "000 000 00 00",
                clientAddress = "-",
            )
    }
}