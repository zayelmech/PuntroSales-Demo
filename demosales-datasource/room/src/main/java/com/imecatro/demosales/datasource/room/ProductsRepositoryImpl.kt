package com.imecatro.demosales.datasource.room

import androidx.annotation.WorkerThread
import com.imecatro.demosales.datasource.room.mappers.toData
import com.imecatro.demosales.datasource.room.mappers.toDomain
import com.imecatro.demosales.datasource.room.mappers.toListDomain
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.*

private const val TAG = "ProductsRepositoryImpl"

class ProductsRepositoryImpl(
    private val productsDao: ProductsDao?
) : ProductsRepository {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override fun addProduct(product: ProductDomainModel?) {
        product?.let {
            productsDao?.addProduct(product = it.toData())
            //Log.d(TAG, "addProduct: ${Uri.parse(it.imageUri)}")
        }
        //TODO implement error
    }

//    private var allProducts: List<ProductDomainModel> = listOf()

    override fun getAllProducts(): Flow<List<ProductDomainModel>> {
        return flow {
            productsDao?.getAllProducts()?.collect {
                emit(it.toListDomain())
            }
            //TODO implement error null
        }
    }

    override fun deleteProductById(id: Int?) {
        id?.let {
            productsDao?.deleteProductById(it)
        }
        //TODO implement error
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override fun updateProduct(product: ProductDomainModel?) {
        product?.let {
            productsDao?.updateProduct(it.toData())
        }
        //TODO implement error null
    }

    override fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return id?.let {
            productsDao?.getProductDetailsById(it)?.toDomain()
        } ?: run {
            null
        }
    }

    override fun searchProducts(letter: String): Flow<List<ProductDomainModel>> {
        return flow {
            productsDao?.searchProducts(letter)?.collect{
                emit(it.toListDomain())
            }
            //TODO implement error null
        }
    }
}

