package com.imecatro.products.ui.add.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.add.mappers.toDomain
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.state.AddProductUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
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
            productsRepository.categories.collect { list ->
                updateState { copy(categories = list.map { d -> d.name }) }
            }
        }
    }

    fun onAddCategory(category: String) {
        viewModelScope.launch {
            productsRepository.addCategory(ProductCategoryDomainModel(name = category))
        }
    }

    fun onCategoryPicked(category: String) {
        viewModelScope.launch {
            updateState { copy(category = category) }
        }
    }
}

