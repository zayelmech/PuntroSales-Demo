package com.imecatro.demosales.ui.clients.details.state

import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

/**
 * State Handler for 3 use cases at least
 */
data class ClientDetailsUiState(
    val clientDetails: ClientDetailsUiModel,
    val isFetchingClientDetails: Boolean = false,
    val error: String? = null,

    val purchases: List<PurchaseUiModel> = emptyList(),

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

    companion object : Idle<ClientDetailsUiState> {
        override val idle: ClientDetailsUiState
            get() {
                val clientDetails = ClientDetailsUiModel()
                return ClientDetailsUiState(clientDetails = clientDetails)
            }

    }
}

