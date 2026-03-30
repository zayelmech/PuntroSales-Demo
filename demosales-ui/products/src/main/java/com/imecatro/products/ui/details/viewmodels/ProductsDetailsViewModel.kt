package com.imecatro.products.ui.details.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.ExportStockHistoryCsvUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.details.mappers.toUiModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProductsDetailsViewModel.Factory::class)
class ProductsDetailsViewModel @AssistedInject constructor(
    @Assisted("productId") private var productId: Long,
    private val productsRepository: ProductsRepository,
    private val exportStockHistoryCsvUseCase: ExportStockHistoryCsvUseCase,
) : BaseViewModel<ProductDetailsUiModel>(ProductDetailsUiModel.idle) {


    override fun onStart() = getDetailsById()

    private fun getDetailsById() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                productsRepository.getProductDetailsById(productId)?.toUiModel()
            response?.let { updateState { response } }
        }
    }

    fun onStockAdded(value: String) = viewModelScope.launch(Dispatchers.IO) {
        val productSelected = productId
        val amount = value.filter { it.isDigit() || it == '.' }
        if (amount.isNotBlank()) {
            productsRepository.addStock(
                reference = "Stock in",
                productId = productSelected,
                amount = amount.toDouble()
            )
            getDetailsById()
        }
    }


    fun onStockRemoved(value: String) = viewModelScope.launch(Dispatchers.IO) {
        val productSelected = productId
        val amount = value.filter { it.isDigit() || it == '.' }
        if (amount.isNotBlank()) {
            productsRepository.removeStock(
                reference = "Stock out",
                productId = productSelected,
                amount = amount.toDouble()
            )
            getDetailsById()
        }
        //TODO else block
    }

    fun onDeleteAction() {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.deleteProductById(productId)
            updateState { copy(productDeleted = true) }
        }
    }

    fun loadDetailsForProduct(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                productsRepository.getProductDetailsById(id)?.toUiModel()
            productId = id
            response?.let { updateState { response } }
        }
    }

    fun onStockHistoryDownload() = viewModelScope.launch(Dispatchers.IO) {
        updateState { copy(isProcessingCsv = true) }
        exportStockHistoryCsvUseCase.execute(productId).onSuccess { file ->
            updateState { copy(file = file, isProcessingCsv = false) }
        }.onFailure {
            updateState { copy(isProcessingCsv = false) }
        }

    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("productId") productId: Long
        ): ProductsDetailsViewModel
    }
}