package com.imecatro.domain.products.usecases

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AddNewProductUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(product: ProductDomainModel) {
        productsRepository.addProduct(product)
    }
}