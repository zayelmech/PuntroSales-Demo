package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.repository.ProductsRepository

/**
 *
 * Add Stock Use Case
 *
 *
 */
class AddStockUseCase(
    private val productsRepository: ProductsRepository
) {

    suspend operator fun invoke(
        reference: String,
        productId: Int,
        amount: Float
    ) {
        productsRepository.addStock(
            reference = reference,
            productId = productId,
            amount = amount
        )

    }

}