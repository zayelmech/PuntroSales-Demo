package com.imecatro.demosales.domain.sales.search

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetProductsByFirstLetterUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(letter: String): List<ProductDomainModel> =

        withContext(dispatcher) {

            return@withContext productsRepository.getProductsStartingWith(letter)

        }


}
