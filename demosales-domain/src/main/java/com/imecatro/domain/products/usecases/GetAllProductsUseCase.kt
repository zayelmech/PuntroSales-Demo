package com.imecatro.domain.products.usecases

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetAllProductsUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Flow<List<ProductDomainModel>> =
        withContext(dispatcher) {
            return@withContext productsRepository.getAllProducts()
        }
}