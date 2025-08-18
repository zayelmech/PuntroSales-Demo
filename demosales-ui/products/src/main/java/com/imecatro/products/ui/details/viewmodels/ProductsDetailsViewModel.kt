package com.imecatro.products.ui.details.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.details.mappers.toUiModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository //= ProductsRepositoryDummyImpl()
) : BaseViewModel<ProductDetailsUiModel>(ProductDetailsUiModel.idle) {

    private val _product: MutableStateFlow<ProductDetailsUiModel> =
        MutableStateFlow(ProductDetailsUiModel.idle)
    val product: StateFlow<ProductDetailsUiModel> = _product.asStateFlow()

    fun getDetailsById(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = productsRepository.getProductDetailsById(id)?.toUiModel()
            _product.update { response!! }
        }
    }

    fun onStockAdded(value: String) = viewModelScope.launch(Dispatchers.IO) {
        val productSelected = product.value?.id
        val amount = value.filter { it.isDigit() || it == '.' }
        if (productSelected != null && amount.isNotBlank()){
            productsRepository.addStock(
                reference = "Stock in",
                productId = productSelected,
                amount = amount.toDouble()
            )
            getDetailsById(productSelected)
        }
        //TODO else block
    }


    fun onStockRemoved(value: String) = viewModelScope.launch(Dispatchers.IO) {
        val productSelected = product.value?.id
        val amount = value.filter { it.isDigit() || it == '.' }
        if (productSelected != null && amount.isNotBlank()){
            productsRepository.removeStock(
                reference = "Stock out",
                productId = productSelected,
                amount = amount.toDouble()
            )
            getDetailsById(productSelected)
        }
        //TODO else block
    }

    fun onDeleteAction(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.deleteProductById(id)
        }
    }

    override fun onStart() = Unit
}