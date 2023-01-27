package com.imecatro.products.ui.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.products.ui.add.model.AddProductUiModel
import kotlinx.coroutines.launch

class AddViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl(),
    private val getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase(),
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase = GetListOfUnitsUseCase()
) : ViewModel() {

    fun onSaveAction(addProductUiModel: AddProductUiModel) {
        viewModelScope.launch {

            productsRepository.addProduct(addProductUiModel.toDomain())
        }

    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase() //listOf("pz", "kg", "g", "m", "cm")
    }

    fun getCurrencies(): List<String> {
        return getListOfCurrenciesUseCase() //listOf("USD", "MXN", "EUR", "GBP")
    }
}

private fun AddProductUiModel.toDomain(): ProductDomainModel? {
    return ProductDomainModel(
        id = null,
        name = this.name,
        price = this.price?.toFloat() ?: 0f,
        currency = this.currency,
        unit = ProductUnit.Default,
        details = this.details,
        imageUri = this.imageUri?.toString()

    )
}
