package com.imecatro.products.ui.update

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.update.model.UpdateProductUiModel

data class UpdateUiState(
    val isFetchingDetails: Boolean,
    val errorFetchingDetails: String?,
    val productDetails: UpdateProductUiModel?,
    val isSavingProduct: Boolean,
    val productUpdated: Boolean,
    val categories: List<String> = emptyList(),
    val errorMsj: String? = null
) : UiState {
    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingDetails || isSavingProduct || productDetails == null
    }

    override fun getError(): ErrorUiModel? {
        if (errorMsj != null) return ErrorUiModel("", errorMsj)
        if (errorFetchingDetails != null) return ErrorUiModel("", errorFetchingDetails)
        return null
    }

    companion object : Idle<UpdateUiState> {
        override val idle: UpdateUiState
            get() = UpdateUiState(
                isFetchingDetails = false,
                errorFetchingDetails = null,
                productDetails = null,
                isSavingProduct = false,
                productUpdated = false
            )
    }
}
