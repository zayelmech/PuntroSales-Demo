package com.imecatro.domain.products.usecases

import com.imecatro.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeleteProductByIdUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(id : Int?) {
        productsRepository.deleteProductById(id)
    }
}