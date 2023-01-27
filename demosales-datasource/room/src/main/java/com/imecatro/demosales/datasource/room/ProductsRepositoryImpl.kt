package com.imecatro.demosales.datasource.room

import com.imecatro.demosales.datasource.room.mappers.toData
import com.imecatro.demosales.datasource.room.mappers.toDomain
import com.imecatro.demosales.datasource.room.mappers.toListDomain
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.collectLatest

class ProductsRepositoryImpl(
    private val productsDao: ProductsDao? = ProductsRoomDatabase.globalDao
) : ProductsRepository {
    override fun addProduct(product: ProductDomainModel?) {
        product?.let {
            productsDao?.addProduct(product = it.toData())
        }
        //TODO implement error
    }

    private var allProducts: List<ProductDomainModel> = listOf()

    override suspend fun getAllProducts(): List<ProductDomainModel> {
        productsDao?.getAllProducts()?.collectLatest {
            allProducts = it.toListDomain()
        }
        return allProducts
    }

    override fun deleteProductById(id: Int?) {
        id?.let {
            productsDao?.deleteProductById(it)
        }
        //TODO implement error
    }

    override fun updateProduct(product: ProductDomainModel?) {
        product?.let {
            productsDao?.updateProduct(it.toData())
        }
        //TODO implement error null
    }

    override  fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return id?.let {
            null //productsDao?.getProductDetailsById(it)?.toDomain()
        } ?: run {
            null
        }
    }
}

