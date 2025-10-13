package com.imecatro.products.ui.list.uistate

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.list.model.ProductUiModel
import java.io.File


data class ListProductsUiState(
    val isFetchingProducts: Boolean,
    val products: List<ProductUiModel>,
    val errorFetchingProducts: ErrorUiModel?,
    val idsSelected: List<Long> = emptyList(),
    val isProcessingCatalog: Boolean = false,
    val catalogFile: File? = null,
    val allSelected: Boolean = false,
    val productsFiltered: List<ProductUiModel>,
    val isSearching: Boolean,
) : UiState {

    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingProducts || isProcessingCatalog
    }

    override fun getError(): ErrorUiModel? {
        return errorFetchingProducts
    }

    companion object : Idle<ListProductsUiState> {
        override val idle: ListProductsUiState
            get() = ListProductsUiState(
                isFetchingProducts = false,
                products = emptyList(),
                errorFetchingProducts = null,
                productsFiltered = emptyList(),
                isSearching = true
            )
    }
}
