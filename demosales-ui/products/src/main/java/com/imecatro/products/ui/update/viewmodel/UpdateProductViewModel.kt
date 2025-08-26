package com.imecatro.products.ui.update.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.update.UpdateUiState
import com.imecatro.products.ui.update.mappers.toDomain
import com.imecatro.products.ui.update.mappers.toUpdateUiModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = UpdateProductViewModel.Factory::class)
class UpdateProductViewModel @AssistedInject constructor(
    @Assisted("productId") private val productId: Long,
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl(),
    private val getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase(),
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase = GetListOfUnitsUseCase()
) : BaseViewModel<UpdateUiState>(UpdateUiState.idle) {

    private val _productSelected: MutableStateFlow<UpdateProductUiModel?> = MutableStateFlow(null)
    val productSelected: StateFlow<UpdateProductUiModel?> = _productSelected.onStart {
        getProductById()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


    fun getProductById() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isFetchingDetails = true) }
            try {
                val response =
                    productsRepository.getProductDetailsById(productId)

                updateState { copy(isFetchingDetails = false) }
                updateState { copy(productDetails = response?.toUpdateUiModel()) }
                if (response == null)
                    updateState { copy(errorFetchingDetails = "No data available") }

            } catch (e: Exception) {
                updateState { copy(isFetchingDetails = false) }
                updateState { copy(errorFetchingDetails = e.message) }
            }
        }

    }

    fun getCurrencies(): List<String> {
        return getListOfCurrenciesUseCase()
    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase()
    }

    fun onSaveAction(updateProductUiModel: UpdateProductUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isSavingProduct = true) }
            productsRepository.updateProduct(updateProductUiModel.copy(id = productId).toDomain())

            updateState { copy(isSavingProduct = false) }
            updateState { copy(productUpdated = true) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("productId") productId: Long
        ): UpdateProductViewModel
    }
}
