package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository

class SearchProductByBarcode(
    private val productsRepository: ProductsRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<String, ProductDomainModel>(coroutineProvider) {


    override suspend fun doInBackground(input: String): ProductDomainModel {
        return productsRepository.searchProductByBarcode(input)
            ?: throw Exception("Product not found")
    }


}