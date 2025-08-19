package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

class GetProductDetailsByIdUseCase(
    val productsRepository: ProductsRepository,
    private val iODispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(id: Long): ProductDomainModel? =
        withContext(iODispatcher) {
            return@withContext try {
                productsRepository.getProductDetailsById(id)

            } catch (e: IOException) {
                null
            }
        }
}
