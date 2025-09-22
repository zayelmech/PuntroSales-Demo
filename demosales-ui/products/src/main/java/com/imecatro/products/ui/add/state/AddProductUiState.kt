package com.imecatro.products.ui.add.state

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState
import com.imecatro.products.ui.categories.model.CategoryUiModel

data class AddProductUiState(
    val productSaved: Boolean = false,

    val category: String = "",
    val categories: List<CategoryUiModel> = emptyList(),
    val isFetchingCategories: Boolean = false,

    val errorMsj: String? = null,
) : UiState {

    val categoriesNames get() = categories.map { it.name }

    override fun isFetchingOrProcessingData(): Boolean {
        return isFetchingCategories
    }

    override fun getError(): ErrorUiModel? {
        if (errorMsj == null) return null
        return ErrorUiModel(message = errorMsj)
    }

    companion object : Idle<AddProductUiState> {
        override val idle: AddProductUiState
            get() = AddProductUiState()

    }

}