package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun addProduct(product: ProductDomainModel?)
     fun getAllProducts(): Flow<List<ProductDomainModel>>
    fun deleteProductById(id :Int?)
    fun updateProduct(product: ProductDomainModel?)
    fun getProductDetailsById(id: Int?): ProductDomainModel?
    fun searchProducts(letter: String): Flow<List<ProductDomainModel>>
}
