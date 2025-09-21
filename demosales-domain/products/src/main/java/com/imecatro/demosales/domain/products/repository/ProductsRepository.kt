package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun addProduct(product: ProductDomainModel)
    fun getAllProducts(): Flow<List<ProductDomainModel>>
    suspend fun deleteProductById(id: Long)
    suspend fun updateProduct(product: ProductDomainModel)
    suspend fun getProductDetailsById(id: Long): ProductDomainModel?

    fun searchProducts(letter: String): Flow<List<ProductDomainModel>>

    suspend fun addStock(reference: String, productId: Long, amount: Double)

    suspend fun removeStock(reference: String,productId: Long, amount: Double)

    val categories: Flow<List<ProductCategoryDomainModel>>

    suspend fun addCategory(category: ProductCategoryDomainModel) : Long

    suspend fun updateCategory(category: ProductCategoryDomainModel)
}
