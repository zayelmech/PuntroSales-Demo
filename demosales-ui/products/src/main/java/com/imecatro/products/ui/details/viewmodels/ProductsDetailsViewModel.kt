package com.imecatro.products.ui.details.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.products.ui.details.mappers.toUiModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsDetailsViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl()
) : ViewModel() {

    private val initDetails = ProductDetailsUiModel(0, "Product", "0.00", "pz", null, "")

    private val _product: MutableStateFlow<ProductDetailsUiModel?> = MutableStateFlow(initDetails)
    val product: StateFlow<ProductDetailsUiModel?> = _product.asStateFlow()

    fun getDetailsById(id: Int?): ProductDetailsUiModel? {
        val response  = productsRepository.getProductDetailsById(id)?.toUiModel()
        _product.value = response
        return response
    }

    fun onDeleteAction(id: Int?) {
        viewModelScope.launch {
            productsRepository.deleteProductById(id)
        }
//            .invokeOnCompletion {
//            getAllProducts()
//        }
    }
}

