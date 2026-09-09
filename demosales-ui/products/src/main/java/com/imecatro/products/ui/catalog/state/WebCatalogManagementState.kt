package com.imecatro.products.ui.catalog.state

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class WebCatalogManagementState(
    val isLoading: Boolean = false,
    val catalogName: String = "",
    val catalogUrl: String = "",
    val lastUpdate: String = "",
    val isPublished: Boolean = false,
    val unpublished: Boolean = false,

    val productIds: Collection<Long> = emptyList(),

    // Stats
    val productsCount: Int = 0,
    val categoriesCount: Int = 0,
    val imagesCount: Int = 0,
    val visitsCount: Int = 0,

    // Activity
    val lastPublicationDate: String = "",
    val lastUpdateDate: String = "",
    val recentlyAddedCount: Int = 0,
    val recentlyModifiedCount: Int = 0,

    val errorMessage: String? = null
) : UiState {

    override fun isFetchingOrProcessingData(): Boolean = isLoading

    override fun getError(): ErrorUiModel? = errorMessage?.let { ErrorUiModel(message = it) }

    companion object : Idle<WebCatalogManagementState> {
        override val idle: WebCatalogManagementState = WebCatalogManagementState()
    }
}
