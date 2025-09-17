package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun addProduct(product: ProductDomainModel?)
    fun getAllProducts(): Flow<List<ProductDomainModel>>
    fun deleteProductById(id: Long)
    fun updateProduct(product: ProductDomainModel?)
    fun getProductDetailsById(id: Long): ProductDomainModel?

    fun searchProducts(letter: String): Flow<List<ProductDomainModel>>

    suspend fun addStock(reference: String, productId: Long, amount: Double)

    suspend fun removeStock(reference: String,productId: Long, amount: Double)
}
