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
                productId = productId?:0L,
                description = "Initial Stock",
                amount = product.stock.quantity,
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

    override fun deleteProductById(id: Long) {
        productsDao?.deleteProductById(id)
    }


    @WorkerThread
    override fun updateProduct(product: ProductDomainModel?) {
        product?.let {
            val currentStock: Double =
                productsDao?.getProductStockHistory(product.id!!)?.sumOf { c -> c.amount } ?: 0.0
            productsDao?.updateProduct(it.toData().copy(stock = currentStock))
        }
        //TODO implement error null
    }

    override fun getProductDetailsById(id: Long): ProductDomainModel? {

        val basicDetails = productsDao?.getProductDetailsById(id)
        val stock = productsDao?.getProductStockHistory(id)

        return basicDetails?.toDomain(stock ?: emptyList())
    }

    override fun searchProducts(letter: String): Flow<List<ProductDomainModel>> {
        if (productsDao == null) throw IOException("DAO ENGINE NOT INITIALIZED")
        return productsDao.searchProducts(letter).map { it.toListDomain() }
    }

    override fun addStock(reference: String, productId: Long, amount: Double) {
        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount,
            date = "",//todo
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStock(stock = stock)
        val currentQty: Double =
            productsDao?.getProductStockHistory(productId)?.sumOf { it.amount } ?: 0.0
        productsDao?.updateProductStock(currentQty, productId)
    }

    override fun removeStock(reference: String, productId: Long, amount: Double) {
        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount * (-1.0),
            date = "",//todo
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStock(stock = stock)
        val currentQty: Double =
            productsDao?.getProductStockHistory(productId)?.sumOf { it.amount } ?: 0.0
        productsDao?.updateProductStock(currentQty, productId)
    }
}