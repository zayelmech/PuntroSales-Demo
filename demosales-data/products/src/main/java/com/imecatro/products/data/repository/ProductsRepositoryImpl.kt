package com.imecatro.products.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.Instant
import java.time.format.DateTimeFormatter

class ProductsRepositoryImpl(
    private val productsDao: ProductsDao?
) : ProductsRepository {

    @WorkerThread
    override fun addProduct(product: ProductDomainModel?) {
        product?.let {
            val productId = productsDao?.addProduct(product = it.toData())

            val stock = StockRoomEntity(
                productId = productId ?: 0L,
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
        try {
            val basicDetails = productsDao?.getProductDetailsById(id)
            val stock = productsDao?.getProductStockHistory(id)
            return basicDetails?.toDomain(stock ?: emptyList())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun searchProducts(letter: String): Flow<List<ProductDomainModel>> {
        if (productsDao == null) throw IOException("DAO ENGINE NOT INITIALIZED")
        return productsDao.searchProducts(letter).map { it.toListDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addStock(reference: String, productId: Long, amount: Double) {
        val now = Instant.now()
        val dateIso = DateTimeFormatter.ISO_INSTANT.format(now) // e.g. "2025-08-24T01:23:45Z"

        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount,
            date = dateIso,// ISO-8601 UTC → ideal for filtering/sorting
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStockAndUpdateProduct(stock = stock)

        if (reference.contains("Stock")) // This will change in future
            productsDao?.rebuildProductStock(productId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun removeStock(reference: String, productId: Long, amount: Double) {
        val now = Instant.now()
        val dateIso = DateTimeFormatter.ISO_INSTANT.format(now) // e.g. "2025-08-24T01:23:45Z"

        val stock = StockRoomEntity(
            productId = productId,
            description = reference,
            amount = amount * (-1.0),
            date = dateIso,// ISO-8601 UTC → ideal for filtering/sorting
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStockAndUpdateProduct(stock = stock)

        if (reference.contains("Stock")) // This will change in future
            productsDao?.rebuildProductStock(productId)
    }
}