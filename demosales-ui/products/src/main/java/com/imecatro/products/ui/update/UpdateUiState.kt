package com.imecatro.products.ui.update

import com.imecatro.products.ui.update.model.UpdateProductUiModel

sealed class UpdateUiState {
    object Loading : UpdateUiState()
    data class Success(val product: UpdateProductUiModel) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
    object Loaded:UpdateUiState()
}
