package com.imecatro.products.ui.categories.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.usecases.AddCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.DeleteCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.UpdateCategoryUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.categories.mappers.toUi
import com.imecatro.products.ui.categories.model.CategoryUiModel
import com.imecatro.products.ui.categories.state.CategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : BaseViewModel<CategoriesUiState>(CategoriesUiState.idle) {

    override fun onStart() {
        viewModelScope.launch {
            getAllCategoriesUseCase().collect { lst ->
                updateState { copy(categories = lst.map { it.toUi() }) }
            }
        }
    }


    fun onEditCategory(categoryUiModel: CategoryUiModel) {
        viewModelScope.launch {
            updateCategoryUseCase.execute {
                id = categoryUiModel.id
                name = categoryUiModel.name
            }
        }
    }

    fun onAddCategory(categoryName: String) {
        viewModelScope.launch {
            addCategoryUseCase
                .execute { name = categoryName }
                .onFailure {
                    updateState { copy(errorMsj = it.message) }
                }
        }
    }

    fun deleteCategory(it: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase.execute { id = it }
        }
    }

}