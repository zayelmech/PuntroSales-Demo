package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository

class UpdateProductUseCase(private val productsRepository: ProductsRepository) {
    operator fun invoke(product: ProductDomainModel) {
        productsRepository.updateProduct(product)
    }

}