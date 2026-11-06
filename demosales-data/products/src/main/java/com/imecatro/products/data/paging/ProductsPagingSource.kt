package com.imecatro.products.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.imecatro.demosales.domain.products.model.ProductListItem
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.model.ProductListRow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/** Bidirectional seek pagination; it never counts or skips earlier rows. */
class ProductsPagingSource(
    private val database: RoomDatabase,
    private val dao: ProductsDao,
    query: ProductsQuery,
) : PagingSource<ProductCursor, ProductListItem>() {
    private val queries = ProductListQueries(query)
    private val observing = AtomicBoolean()
    private val observationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val observationReady = CompletableDeferred<Unit>()

    init {
        registerInvalidatedCallback {
            observationScope.cancel()
            observationReady.cancel()
        }
    }

    private fun observeChanges() {
        observationScope.launch {
            try {
                database.invalidationTracker.createFlow("products_table", "categories_table").collect {
                    if (!observationReady.complete(Unit)) invalidate()
                }
            } catch (cancelled: CancellationException) {
                observationReady.cancel(cancelled)
            } catch (error: Exception) {
                if (!observationReady.completeExceptionally(error)) invalidate()
            }
        }
    }

    override suspend fun load(params: LoadParams<ProductCursor>): LoadResult<ProductCursor, ProductListItem> =
        withContext(Dispatchers.IO) {
            try {
                // Register before reading. A concurrent mutation invalidates the entire generation.
                if (invalid) return@withContext LoadResult.Invalid()
                if (observing.compareAndSet(false, true)) observeChanges()
                observationReady.await()
                val result = database.withTransaction {
                    when {
                        params is LoadParams.Refresh && params.key?.anchor == true -> refreshAround(params.key!!, params.loadSize)
                        else -> loadPage(params.key, params.loadSize)
                    }
                }
                if (invalid) LoadResult.Invalid() else result
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                LoadResult.Error(error)
            }
        }

    private suspend fun loadPage(cursor: ProductCursor?, size: Int): LoadResult.Page<ProductCursor, ProductListItem> {
        val fetched = dao.getProductListRows(queries.rows(size + 1, cursor))
        val more = fetched.size > size
        val rows = fetched.take(size).let { if (cursor?.before == true) it.reversed() else it }
        return page(rows,
            hasBefore = if (cursor?.before == true) more else cursor != null && rows.isNotEmpty(),
            hasAfter = if (cursor?.before == true) rows.isNotEmpty() else more)
    }

    private suspend fun refreshAround(cursor: ProductCursor, size: Int): LoadResult.Page<ProductCursor, ProductListItem> {
        // Re-read the anchor: a price, stock, name or category edit may have moved it.
        val current = dao.getProductListRowById(cursor.id)?.let { ProductCursor.from(it) } ?: cursor
        val beforeSize = size / 2
        val previous = dao.getProductListRows(queries.rows(beforeSize + 1, current.copy(before = true)))
        val before = previous.take(beforeSize).reversed()
        val afterSize = size - before.size
        val following = dao.getProductListRows(queries.rows(afterSize + 1, current.copy(before = false), inclusive = true))
        return page(before + following.take(afterSize), previous.size > beforeSize, following.size > afterSize)
    }

    private fun page(rows: List<ProductListRow>, hasBefore: Boolean, hasAfter: Boolean) = LoadResult.Page(
        data = rows.map { it.toListItem() },
        prevKey = rows.firstOrNull()?.takeIf { hasBefore }?.let { ProductCursor.from(it, before = true) },
        nextKey = rows.lastOrNull()?.takeIf { hasAfter }?.let { ProductCursor.from(it) },
    )

    override fun getRefreshKey(state: PagingState<ProductCursor, ProductListItem>): ProductCursor? =
        state.anchorPosition?.let { state.closestItemToPosition(it) }?.let {
            ProductCursor(it.id, it.name.lowercase(), it.price, it.stock, anchor = true)
        }
}
