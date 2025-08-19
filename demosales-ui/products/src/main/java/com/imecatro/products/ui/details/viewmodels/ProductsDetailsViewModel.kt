package com.imecatro.products.ui.details.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.details.mappers.toUiModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = ProductsDetailsViewModel.Factory::class)
class ProductsDetailsViewModel @AssistedInject constructor(
    @Assisted("productId") private val productId: Long,
    private val productsRepository: ProductsRepository
) : BaseViewModel<ProductDetailsUiModel>(ProductDetailsUiModel.idle) {

    private val _product: MutableStateFlow<ProductDetailsUiModel> =
        MutableStateFlow(ProductDetailsUiModel.idle)
    val product: StateFlow<ProductDetailsUiModel> = _product.onStart {
        getDetailsById()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ProductDetailsUiModel.idle)


    private fun getDetailsById() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                productsRepository.getProductDetailsById(productId)?.toUiModel()
            response?.let { _product.update { response } }
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
            _product.update { it.copy(productDeleted = true) }
        }
    }

    override fun onStart() = Unit

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("productId") productId: Long
        ): ProductsDetailsViewModel
    }
}