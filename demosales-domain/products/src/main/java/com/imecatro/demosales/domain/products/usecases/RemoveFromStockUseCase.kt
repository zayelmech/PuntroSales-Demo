package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.repository.ProductsRepository

class RemoveFromStockUseCase(
    private val productsRepository: ProductsRepository
) {

    suspend operator fun invoke(
        reference: String,
        productId: Long,
        amount: Double
    ) {
        productsRepository.removeStock(
            reference = reference,
            productId = productId,
            amount = amount
        )

    }
}
