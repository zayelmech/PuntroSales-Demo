package com.imecatro.products.ui.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.products.ui.details.mappers.toUiModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import kotlinx.coroutines.launch

class ProductsDetailsViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl()
) : ViewModel() {



    fun getDetailsById(id: Int?): ProductDetailsUiModel? {

        return productsRepository.getProductDetailsById(id)?.toUiModel()

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

