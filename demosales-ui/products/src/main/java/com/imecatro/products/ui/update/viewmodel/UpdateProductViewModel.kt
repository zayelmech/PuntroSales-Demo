package com.imecatro.products.ui.update.viewmodel

import androidx.lifecycle.ViewModel
import com.imecatro.domain.products.repository.ProductsRepository
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.domain.products.usecases.GetListOfCurrenciesUseCase
import com.imecatro.domain.products.usecases.GetListOfUnitsUseCase
import com.imecatro.products.ui.update.mappers.toDomain
import com.imecatro.products.ui.update.mappers.toUpdateUiModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel

class UpdateProductViewModel(
    private val productsRepository: ProductsRepository = ProductsRepositoryDummyImpl(),
    private val getListOfCurrenciesUseCase: GetListOfCurrenciesUseCase = GetListOfCurrenciesUseCase(),
    private val getListOfUnitsUseCase: GetListOfUnitsUseCase = GetListOfUnitsUseCase()
) : ViewModel() {
    fun getCurrencies(): List<String> {
        return getListOfCurrenciesUseCase()
    }

    fun getUnities(): List<String> {
        return getListOfUnitsUseCase()
    }

    fun onSaveAction(updateProductUiModel: UpdateProductUiModel) {
        productsRepository.updateProduct(updateProductUiModel.toDomain())
    }

    fun getProductById(productId: Int?): UpdateProductUiModel {
        return  productsRepository.getProductDetailsById(productId)!!.toUpdateUiModel()
    }
}
