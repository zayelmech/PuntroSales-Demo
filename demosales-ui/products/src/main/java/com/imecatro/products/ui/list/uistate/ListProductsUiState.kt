package com.imecatro.products.ui.list.uistate

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.list.model.ProductUiModel

data class ListProductsUiState(
    val isFetchingProducts: Boolean,
    val products: List<ProductUiModel>,
    val errorFetchingProducts: ErrorUiModel?
) : UiState {
    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingProducts
    }

    override fun getError(): ErrorUiModel? {
        return errorFetchingProducts
    }

    companion object : Idle<ListProductsUiState> {
        override val idle: ListProductsUiState
            get() = ListProductsUiState(
                isFetchingProducts = false,
                products = emptyList(),
                errorFetchingProducts = null
            )
    }
}
