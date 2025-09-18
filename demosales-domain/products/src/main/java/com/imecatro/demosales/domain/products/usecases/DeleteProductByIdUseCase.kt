package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DeleteProductByIdUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id : Long) {
        productsRepository.deleteProductById(id)
    }
}