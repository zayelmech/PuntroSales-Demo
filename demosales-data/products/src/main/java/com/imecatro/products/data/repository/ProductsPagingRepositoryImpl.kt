package com.imecatro.products.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.RoomDatabase
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.demosales.domain.products.repository.ProductsPagingRepository
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.paging.ProductListQueries
import com.imecatro.products.data.paging.ProductsPagingSource
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import java.util.concurrent.atomic.AtomicReference

class ProductsPagingRepositoryImpl(
    private val database: RoomDatabase,
    private val dao: ProductsDao,
) : ProductsPagingRepository {
    override fun getProductsPaged(query: ProductsQuery) = flow {
        val current = AtomicReference<ProductsPagingSource?>()
        try {
            emitAll(Pager(
                config = PagingConfig(pageSize = 40, initialLoadSize = 40, prefetchDistance = 12,
                    maxSize = 200, enablePlaceholders = false),
                pagingSourceFactory = {
                    createPagingSource(query).also { current.getAndSet(it)?.invalidate() }
                },
            ).flow)
        } finally {
            // Query changes and ViewModel disposal release Room's public Flow observer.
            current.getAndSet(null)?.invalidate()
        }
    }

    fun createPagingSource(query: ProductsQuery) = ProductsPagingSource(database, dao, query)

    override fun observeCount(query: ProductsQuery) = dao.observeProductListCount(ProductListQueries(query).count())

    override suspend fun getMatchingIds(query: ProductsQuery) = dao.getProductListIds(ProductListQueries(query).ids())
}
