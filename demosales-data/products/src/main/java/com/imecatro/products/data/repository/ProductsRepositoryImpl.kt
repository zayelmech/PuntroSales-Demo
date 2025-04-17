package com.imecatro.products.data.repository

import androidx.annotation.WorkerThread
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.mappers.toData
import com.imecatro.products.data.mappers.toDomain
import com.imecatro.products.data.mappers.toListDomain
import com.imecatro.products.data.model.StockRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class ProductsRepositoryImpl(
    private val productsDao: ProductsDao?
) : ProductsRepository {

    @WorkerThread
    override fun addProduct(product: ProductDomainModel?) {
        product?.let {
            val productId = productsDao?.addProduct(product = it.toData())

            val stock = StockRoomEntity(
                productId = productId?.toInt() ?: 0,
                description = "Initial Stock",
                amount = product.stock.quantity.toFloat(),
                date = "",
                timeStamp = System.currentTimeMillis().toString()
            )
            productsDao?.addStock(stock = stock)
        }
        //TODO implement error
    }

//    private var allProducts: List<ProductDomainModel> = listOf()

    override fun getAllProducts(): Flow<List<ProductDomainModel>> {
        if (productsDao == null) throw IOException("DAO ENGINE NOT INITIALIZED")

        return productsDao.getAllProducts().map { it.toListDomain() }
    }

    override fun deleteProductById(id: Int?) {
        id?.let {
            productsDao?.deleteProductById(it)
        }
        //TODO implement error
    }

    @WorkerThread
    override fun updateProduct(product: ProductDomainModel?) {
        product?.let {
            productsDao?.updateProduct(it.toData())
        }
        //TODO implement error null
    }

    override fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return id?.let {
            val basicDetails = productsDao?.getProductDetailsById(it)
            val stock = productsDao?.getProductStockHistory(it)

            return basicDetails?.toDomain(stock ?: emptyList())
        } ?: run {
            null
        }
    }

    override fun searchProducts(letter: String): Flow<List<ProductDomainModel>> {
        if (productsDao == null) throw IOException("DAO ENGINE NOT INITIALIZED")
        return productsDao.searchProducts(letter).map { it.toListDomain() }
    }

    override fun addStock(reference: String, productId: Int, amount: Float) {
        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount,
            date = "",//todo
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStock(stock = stock)
        val currentQty: Double = productsDao?.getProductDetailsById(productId)?.stock ?: 0.0
        val new: Double = currentQty + amount
        productsDao?.updateProductStock(new, productId)
    }

    override fun removeStock(reference: String, productId: Int, amount: Float) {
        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount * (-1f),
            date = "",//todo
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStock(stock = stock)
        val currentQty: Double = productsDao?.getProductDetailsById(productId)?.stock ?: 0.0
        val new: Double = currentQty - amount
        productsDao?.updateProductStock(new, productId)
    }
}