package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.products.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.products.repository.ProductsRepository

class UpdateProductUseCase(private val productsRepository: ProductsRepository) {
    operator fun invoke(product: ProductDomainModel) {
        productsRepository.updateProduct(product)
    }

}