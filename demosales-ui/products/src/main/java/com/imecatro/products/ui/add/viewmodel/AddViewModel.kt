package com.imecatro.products.ui.add.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.AddCategoryUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.add.mappers.toDomain
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.state.AddProductUiState
import com.imecatro.products.ui.categories.mappers.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase
) : BaseViewModel<AddProductUiState>(AddProductUiState.idle) {

    fun onSaveAction(addProductUiModel: AddProductUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.addProduct(addProductUiModel.toDomain())
        }

    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase() //listOf("pz", "kg", "g", "m", "cm")
    }


    override fun onStart() {
        viewModelScope.launch {
            getAllCategoriesUseCase().collect { list ->
                updateState { copy(categories = list.map { it.toUi() }) }
            }
        }
    }

    fun onAddCategory(category: String) {
        viewModelScope.launch {
            addCategoryUseCase
                .execute {
                    name = category
                }.onSuccess {
                    updateState { copy(category = category) }
                }.onFailure {
                    updateState { copy(errorMsj = it.message) }
                }
        }
    }

    fun onCategoryPicked(category: String) {
        viewModelScope.launch {
            updateState { copy(category = category) }
        }
    }

    fun clearError() {
        viewModelScope.launch {
            updateState { copy(errorMsj = null) }
        }
    }
}

