package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface ProductsRepository {
    suspend fun addProduct(product: ProductDomainModel)
    fun getAllProducts(): Flow<List<ProductDomainModel>>
    suspend fun countProducts(): Int = getAllProducts().first().size
    suspend fun deleteProductById(id: Long)
    suspend fun updateProduct(product: ProductDomainModel)
    suspend fun getProductDetailsById(id: Long): ProductDomainModel?

    fun searchProducts(letter: String): Flow<List<ProductDomainModel>>

    suspend fun searchProductByBarcode(barcode: String): ProductDomainModel?

    suspend fun addStock(reference: String, productId: Long, amount: Double)

    suspend fun removeStock(reference: String,productId: Long, amount: Double)
    fun getProductsWithIds(ids: List<Long>) : List<ProductDomainModel>
    /** Export fields and current stock, without loading movement histories. */
    fun getProductsForExport(ids: List<Long>): List<ProductDomainModel> = getProductsWithIds(ids)
}
