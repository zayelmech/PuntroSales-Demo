package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl()
) : ViewModel() {


    private val _productsList: MutableStateFlow<List<ProductUiModel>> =
        MutableStateFlow(listOf())

    val productsList: StateFlow<List<ProductUiModel>> = _productsList.asStateFlow()

     fun getAllProducts() {
        viewModelScope.launch {
            _productsList.emit(productsRepository.getAllProducts().toProductUiModel())
        }
    }

    fun getDetailsById(id: Int?): ProductUiModel? {

        return _productsList.value.find { it.id == id }

    }

    fun onDeleteAction(id: Int?) {
        viewModelScope.launch {
            productsRepository.deleteProductById(id)
        }.invokeOnCompletion {
            getAllProducts()
        }
    }
}

