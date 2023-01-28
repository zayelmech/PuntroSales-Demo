package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository //= ProductsRepositoryDummyImpl()
) : ViewModel() {


    private val _productsList: MutableStateFlow<List<ProductUiModel>> =
        MutableStateFlow(listOf())

    val productsList: StateFlow<List<ProductUiModel>> = _productsList.asStateFlow()

    fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {

            productsRepository.getAllProducts().collectLatest { list ->
                _productsList.emit(list.toProductUiModel())
            }

        }
    }


}

