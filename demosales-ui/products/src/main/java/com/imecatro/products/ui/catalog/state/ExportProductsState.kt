package com.imecatro.products.ui.catalog.state

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.list.model.ProductUiModel

data class ExportProductsState(
    val ids: List<Long> = emptyList(),
    val isProcessingCatalog: Boolean = false,
    val catalogReady: Boolean = false,
    val errorMessage: String? = null,
    val previewUrl: String = "",
    val catalogUrl: String = "",
    val products: Map<String?, List<ProductUiModel>> = mapOf(),

    // Store Info
    val storeName: String = "",
    val storeDescription: String = "",
    val storeLogoUrl: String? = null,
    val currency: String = "MXN",
    val whatsapp: String = "",
    val location: String = "",
    val template: String = "store", // Default option
    val enableStock: Boolean = false,

    // Selection state
    val selectedProductIds: Set<Long> = emptySet()
) : UiState {

    val selectedProducts: List<ProductUiModel>
        get() = products.values.flatten().filter { it.id in selectedProductIds }

    val selectedCount get() = selectedProducts.size
    val categoriesCount get() = selectedProducts.map { it.category }.distinct().size
    val withImageCount get() = selectedProducts.count { it.imageUrl.toString().isNotBlank() }
    val withoutImageCount get() = selectedProducts.count { it.imageUrl == null || it.imageUrl.toString().isBlank() }
    val unavailableCount get() = 0 // Assuming all are available for now or based on stock

    val step: Int get() {
        return when {
            isProcessingCatalog -> 3
            catalogReady -> 4
            products.isNotEmpty() -> 2
            else -> 1
        }
    }

    override fun isFetchingOrProcessingData(): Boolean {
        return isProcessingCatalog
    }

    override fun getError(): ErrorUiModel? {
        return errorMessage?.let { ErrorUiModel(message = it) }
    }

    companion object : Idle<ExportProductsState> {
        override val idle: ExportProductsState
            get() = ExportProductsState()
    }
}