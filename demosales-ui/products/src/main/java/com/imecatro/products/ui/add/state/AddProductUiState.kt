package com.imecatro.products.ui.add.state

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class AddProductUiState(
    val productSaved: Boolean = false,

    val category: String = "",
    val categories: List<String> = emptyList(),
    val isFetchingCategories: Boolean = false,
) : UiState {


    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingCategories
    }

    override fun getError(): ErrorUiModel? {
        return null
    }

    companion object : Idle<AddProductUiState> {
        override val idle: AddProductUiState
            get() = AddProductUiState()

    }

}