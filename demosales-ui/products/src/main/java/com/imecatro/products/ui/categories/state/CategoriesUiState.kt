package com.imecatro.products.ui.categories.state

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.categories.model.CategoryUiModel

data class CategoriesUiState(
    val categories: List<CategoryUiModel> = emptyList(),
    val errorMsj : String? = null
) : UiState {
    override fun isFetchingOrProcessingData(): Boolean {
        return false
    }

    override fun getError(): ErrorUiModel? {
        if (errorMsj != null)
            return ErrorUiModel(message = errorMsj)

        return null
    }

    companion object : Idle<CategoriesUiState> {
        override val idle: CategoriesUiState
            get() = CategoriesUiState()
    }
}