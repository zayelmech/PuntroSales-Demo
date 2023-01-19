package com.imecatro.domain.products.usecases

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.repository.ProductsRepository

class UpdateProductUseCase(private val productsRepository: ProductsRepository) {
    operator fun invoke(product: ProductDomainModel) {
        productsRepository.updateProduct(product)
    }

}