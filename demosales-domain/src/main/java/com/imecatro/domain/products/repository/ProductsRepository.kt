package com.imecatro.domain.products.repository

import com.imecatro.domain.products.model.ProductDomainModel

interface ProductsRepository {
    fun addProduct(product: ProductDomainModel?)
    suspend fun getAllProducts(): List<ProductDomainModel>
    fun deleteProductById(id :Int?)
    fun updateProduct(product: ProductDomainModel?)
    fun getProductDetailsById(id: Int?): ProductDomainModel?
}
