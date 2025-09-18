package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetProductsLikeUseCase(
    private val productsRepository: ProductsRepository,
) {
    operator fun invoke(productName: String): Flow<List<ProductDomainModel>> =productsRepository.searchProducts(productName)
}