package com.imecatro.demosales.products

import android.os.SystemClock
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.datasource.AppRoomDatabase
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.products.data.paging.ProductCursor
import com.imecatro.products.data.repository.ProductsPagingRepositoryImpl
import com.imecatro.products.ui.list.mappers.toProductUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class ProductsPagingPerformanceTest {
    @Test fun recordPagedAndCachedLoading() = runBlocking(Dispatchers.IO) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (size in listOf(1_000, 10_000, 50_000)) {
            val name = "products-paged-performance-$size.db"
            context.deleteDatabase(name)
            val rowReads = AtomicInteger()
            val database = Room.databaseBuilder(context, AppRoomDatabase::class.java, name)
                .setQueryCallback({ sql, _ -> if (sql.startsWith("SELECT p.id,p.name,p.nameKey")) rowReads.incrementAndGet() }, Executor { it.run() })
                .build()
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            try {
                seedProducts(database, size)
                val repository = ProductsPagingRepositoryImpl(database, database.productsRoomDao())
                for (deep in listOf(false, true)) {
                    suspend fun load() {
                        val source = repository.createPagingSource(ProductsQuery())
                        val result = if (deep) source.load(PagingSource.LoadParams.Append(
                            ProductCursor((size - 80).toLong(), "", 0.0, 0.0), 40, false))
                        else source.load(PagingSource.LoadParams.Refresh(null, 40, false))
                        assertTrue(result.toString(), result is PagingSource.LoadResult.Page)
                        val models = (result as PagingSource.LoadResult.Page).data.map { it.toProductUiModel() }
                        assertEquals(40, models.size)
                        assertEquals(if (deep) (size - 79).toLong() else 1L, models.first().id)
                        source.invalidate()
                    }
                    repeat(2) { load() }
                    val samples = (1..9).map {
                        val start = SystemClock.elapsedRealtimeNanos()
                        load()
                        (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                    }
                    reportMetric(if (deep) "paged_deep_models" else "paged_first_query_models", size, 40, samples)
                }
                // Include Pager collection and presentation, not only the underlying SQL load.
                suspend fun firstGeneration() = repository.getProductsPaged(ProductsQuery())
                    .map { page -> page.map { it.toProductUiModel() } }.asSnapshot()
                repeat(2) { assertEquals(40, firstGeneration().size) }
                val generationSamples = (1..9).map {
                    val start = SystemClock.elapsedRealtimeNanos()
                    assertEquals(40, firstGeneration().size)
                    (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                }
                reportMetric("paged_initial_models", size, 40, generationSamples)
                val cached = repository.getProductsPaged(ProductsQuery())
                    .map { page -> page.map { it.toProductUiModel() } }.cachedIn(scope)
                val first = cached.asSnapshot()
                repeat(2) { assertEquals(first, cached.asSnapshot()) }
                val before = rowReads.get()
                val samples = (1..9).map {
                    val start = SystemClock.elapsedRealtimeNanos()
                    assertEquals(first, cached.asSnapshot())
                    (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                }
                assertEquals("Cached recollection must not re-query Room", before, rowReads.get())
                reportMetric("cached_recollection_models", size, first.size, samples)
            } finally {
                scope.cancel()
                database.close()
                context.deleteDatabase(name)
            }
        }
    }
}
