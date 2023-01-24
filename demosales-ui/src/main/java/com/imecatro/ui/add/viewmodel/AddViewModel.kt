package com.imecatro.ui.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.ui.add.model.AddProductUiModel
import kotlinx.coroutines.launch

class AddViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl()
) : ViewModel() {

    fun onSaveAction(addProductUiModel: AddProductUiModel) {
        viewModelScope.launch {

            productsRepository.addProduct(addProductUiModel.toDomain())
        }

    }

    fun getUnities(): List<String> {
        return listOf("pz", "kg", "g", "m", "cm")
    }

    fun getCurrencies(): List<String> {
        return listOf("USD", "MXN", "EUR", "GBP")
    }
}

private fun AddProductUiModel.toDomain(): ProductDomainModel? {
    return ProductDomainModel(
        id = null ,
        name =this.name,
        price =this.price?.toFloat() ?: 0f,
        currency =this.currency,
        unit =ProductUnit.Default,
        details =this.details,
        imageUri = this.imageUri?.toString()

    )
}
