package com.imecatro.demosales.domain.products.search

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

class GetProductsLikeUseCase(
    private val productsRepository: ProductsRepository,
) {
    operator fun invoke(productName: String): Flow<List<ProductDomainModel>> =productsRepository.searchProducts(productName)
//        flow {
//            try {
//
//
//            } catch (e: Exception) {
//                println(e.message)
//                //emit(emptyList())
//            }
//        }
}
