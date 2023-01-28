package com.imecatro.products.ui.update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.products.ui.update.UpdateUiState
import com.imecatro.products.ui.update.mappers.toDomain
import com.imecatro.products.ui.update.mappers.toUpdateUiModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateProductViewModel @Inject constructor(
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl(),
    private val getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase(),
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase = GetListOfUnitsUseCase()
) : ViewModel() {

    private val _productSelected: MutableStateFlow<UpdateProductUiModel?> = MutableStateFlow(null)
    val productSelected: StateFlow<UpdateProductUiModel?> = _productSelected.asStateFlow()

    private val _uiState: MutableStateFlow<UpdateUiState> = MutableStateFlow(UpdateUiState.Loading)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()
    fun getProductById(productId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response =
                    productsRepository.getProductDetailsById(productId)!!.toUpdateUiModel()
                _productSelected.value = response
                _uiState.value = UpdateUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UpdateUiState.Error(e.message ?: "Error null")
            }
        }

    }

    fun onProductLoaded() {
        _uiState.value = UpdateUiState.Loaded
    }

    fun getCurrencies(): List<String> {
        return getListOfCurrenciesUseCase()
    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase()
    }


    fun onSaveAction(updateProductUiModel: UpdateProductUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.updateProduct(updateProductUiModel.toDomain())
        }
    }
}
