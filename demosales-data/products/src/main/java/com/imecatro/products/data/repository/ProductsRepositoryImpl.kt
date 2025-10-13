package com.imecatro.products.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.products.data.datasource.CategoriesDao
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.mappers.toData
import com.imecatro.products.data.mappers.toDomain
import com.imecatro.products.data.mappers.toProductListDomain
import com.imecatro.products.data.mappers.toProductsListDomain
import com.imecatro.products.data.model.StockRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter

class ProductsRepositoryImpl(
    private val productsDao: ProductsDao?,
    private val categoriesDao: CategoriesDao?
) : ProductsRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun addProduct(product: ProductDomainModel) {

        val productId = productsDao?.addProduct(product = product.toData())

        val now = Instant.now()
        val dateIso = DateTimeFormatter.ISO_INSTANT.format(now) // e.g. "2025-08-24T01:23:45Z"

        val stock = StockRoomEntity(
            productId = productId ?: 0L,
            description = "Initial Stock",
            amount = product.stock.quantity,
            date = dateIso,
            timeStamp = System.currentTimeMillis().toString()
        )
        productsDao?.addStock(stock = stock)

        val categoryName = product.category?.name
        if (categoryName.isNullOrBlank()) return

        val category = categoriesDao?.getCategoryByName(categoryName)

        if (category != null && category.id > 0 && productId != null)
            categoriesDao.assignCategory(productId, category.id)
    }

    override fun getAllProducts(): Flow<List<ProductDomainModel>> =
        productsDao?.getProductsWithCategories()?.map { it.toProductListDomain() } ?: emptyFlow()

    override suspend fun deleteProductById(id: Long) {
        productsDao?.deleteProductById(id)
    }


    @WorkerThread
    override suspend fun updateProduct(product: ProductDomainModel) {
        val categoryId: Long? = product.category?.let { findCategoryIdByName(it.name)}

        productsDao?.updateProduct(product.toData().copy(categoryId = categoryId))

        productsDao?.rebuildProductStock(product.id!!)

    }

    suspend fun findCategoryIdByName(name: String): Long? {
        if (name.isBlank()) return null

        return categoriesDao?.getCategoryByName(name)?.id
    }

    override suspend fun getProductDetailsById(id: Long): ProductDomainModel? {
        return try {
            productsDao?.getProductFullDetailsByd(id)?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun searchProducts(letter: String): Flow<List<ProductDomainModel>> {
        if (productsDao == null) throw IOException("DAO ENGINE NOT INITIALIZED")
        return productsDao.searchProducts(letter).map { it.toProductsListDomain() }
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

    override fun getProductsWithIds(ids: List<Long>) : List<ProductDomainModel> {
        return ids.mapNotNull { productsDao?.getProductFullDetailsByd(it)?.toDomain() }
    }
}