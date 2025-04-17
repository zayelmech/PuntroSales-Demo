package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun addProduct(product: ProductDomainModel?)
    fun getAllProducts(): Flow<List<ProductDomainModel>>
    fun deleteProductById(id: Int?)
    fun updateProduct(product: ProductDomainModel?)
    fun getProductDetailsById(id: Int?): ProductDomainModel?

    fun searchProducts(letter: String): Flow<List<ProductDomainModel>>

    fun addStock(reference: String, productId: Int, amount: Float)

    fun removeStock(reference: String,productId: Int, amount: Float)
}
