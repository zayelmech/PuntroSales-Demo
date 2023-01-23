package com.imecatro.ui.products.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.ui.products.mappers.toProductUiModel
import com.imecatro.ui.products.model.ProductUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

