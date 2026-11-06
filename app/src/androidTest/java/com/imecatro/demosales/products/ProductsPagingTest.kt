package com.imecatro.demosales.products

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.PagingConfig
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.datasource.AppRoomDatabase
import com.imecatro.demosales.datasource.MIGRATION_14_15
import com.imecatro.demosales.domain.products.model.ProductListItem
import com.imecatro.demosales.domain.products.model.ProductOrder
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity
import com.imecatro.products.data.paging.ProductCursor
import com.imecatro.products.data.paging.productSearchKey
import com.imecatro.products.data.paging.ProductsPagingSource
import com.imecatro.products.data.repository.ProductsPagingRepositoryImpl
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProductsPagingTest {
    @get:Rule val migrations = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppRoomDatabase::class.java)
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "products-regression-${UUID.randomUUID()}.db"
    private lateinit var database: AppRoomDatabase
    private lateinit var repository: ProductsPagingRepositoryImpl
    private val sources = mutableListOf<ProductsPagingSource>()

    @Before fun open() {
        database = Room.databaseBuilder(context, AppRoomDatabase::class.java, name).build()
        seedProducts(database, 121)
        repository = ProductsPagingRepositoryImpl(database, database.productsRoomDao())
    }

    @After fun close() {
        sources.forEach { it.invalidate() }
        database.close()
        context.deleteDatabase(name)
    }

    @Test fun everyOrderingAndCategoryMatchesTheFullCatalogWithoutDuplicates() = runBlocking(Dispatchers.IO) {
        val legacy = ProductsRepositoryImpl(database.productsRoomDao(), database.categoriesRoomDao()).getAllProducts().first()
        for (order in ProductOrder.entries) for (descending in listOf(false, true)) {
            for (filter in listOf(ProductsQuery(), ProductsQuery(categoryNames = setOf("Food")),
                ProductsQuery(includeUncategorized = true), ProductsQuery(categoryNames = setOf("Food", "Other")),
                ProductsQuery(categoryNames = setOf("Food"), includeUncategorized = true))) {
                val query = filter.copy(order = order, descending = descending)
                val filtered = legacy.filter {
                    (filter.categoryNames.isEmpty() && !filter.includeUncategorized) ||
                        it.category?.name in filter.categoryNames || (filter.includeUncategorized && it.category == null)
                }
                val comparator = when (order) {
                    ProductOrder.NAME -> compareBy<com.imecatro.demosales.domain.products.model.ProductDomainModel> { it.name?.lowercase() }
                    ProductOrder.PRICE -> compareBy { it.price }
                    ProductOrder.STOCK -> compareBy { it.stock.quantity }
                    ProductOrder.CREATED -> compareBy { it.id }
                }.thenBy { it.id }
                val expected = filtered.sortedWith(if (descending) comparator.reversed() else comparator).map { it.id }
                val source = repository.createPagingSource(query).also { sources += it }
                val actual = mutableListOf<ProductListItem>()
                var page = load(source)
                actual += page.data
                while (page.nextKey != null) {
                    page = load(source, page.nextKey)
                    actual += page.data
                }
                assertEquals("$query", expected, actual.map { it.id })
                assertEquals(actual.size, actual.map { it.id }.toSet().size)
                assertEquals(expected, repository.getMatchingIds(query))
                assertEquals(expected.size, repository.observeCount(query).first())
                val backwards = page.data.toMutableList()
                while (page.prevKey != null) {
                    val key = page.prevKey!!
                    page = source.load(PagingSource.LoadParams.Prepend(key, 17, false)) as PagingSource.LoadResult.Page
                    backwards.addAll(0, page.data)
                }
                assertEquals(expected, backwards.map { it.id })
            }
        }
    }

    @Test fun prefixMatchingKeepsLegacyCaseWildcardsAndUnicodeBehavior() = runBlocking(Dispatchers.IO) {
        val dao = database.productsRoomDao()
        listOf("Apple", "apple", "ÁRBOL", "árbol", "Milk_1", "Milk%2", "Kelvin", "KELVIN", "kelvin", "İtem", "item", "ſtock", "stock").forEach {
            dao.addProduct(ProductRoomEntity(name = it, price = 1.0, currency = "MXN", unit = "pz", stock = 2.0, details = "", imageUri = ""))
        }
        for (prefix in listOf("Ap", "ap", "Á", "á", "Milk_", "%", "missing", "Product 0000", "k", "i", "s")) {
            val expected = dao.searchProducts(prefix).first().map { it.id }.sorted()
            val actual = repository.getMatchingIds(ProductsQuery(search = prefix))
            assertEquals(prefix, expected, actual)
        }
    }

    @Test fun stockAndCategoryWritesInvalidateCachedGenerationAndRefreshAroundTheAnchor() = runBlocking(Dispatchers.IO) {
        val source = repository.createPagingSource(ProductsQuery(order = ProductOrder.STOCK)).also { sources += it }
        val first = load(source)
        val anchor = source.getRefreshKey(PagingState(listOf(first), 8, PagingConfig(17), 0))!!
        database.productsRoomDao().updateProductStock(500.0, anchor.id)
        withTimeout(5_000) { while (!source.invalid) delay(10) }
        val replacement = repository.createPagingSource(ProductsQuery(order = ProductOrder.STOCK)).also { sources += it }
        val refreshed = replacement.load(PagingSource.LoadParams.Refresh(anchor, 17, false)) as PagingSource.LoadResult.Page
        assertEquals(500.0, refreshed.data.first { it.id == anchor.id }.stock, 0.0)
        database.categoriesRoomDao().clearCategory(1L)
        withTimeout(5_000) { while (!replacement.invalid) delay(10) }
    }

    @Test fun migrationBackfillsUnicodeKeysAndPreservesInventory() {
        val migrationName = "products-migration-${UUID.randomUUID()}.db"
        try {
            migrations.createDatabase(migrationName, 14).apply {
                execSQL("INSERT INTO categories_table(id,name) VALUES(1,'Food')")
                execSQL("INSERT INTO products_table(id,name,price,currency,unit,stock,details,imageUri,category_id,barcode) VALUES(7,'ÁRBOL',12.5,'MXN','kg',-3.5,'Original','original.jpg',1,'ABC')")
                close()
            }
            migrations.runMigrationsAndValidate(migrationName, 15, true, MIGRATION_14_15).use { db ->
                db.query("SELECT id,name,nameKey,searchName,price,stock,details,category_id,barcode FROM products_table").use {
                    assertTrue(it.moveToFirst())
                    assertEquals(7L, it.getLong(0))
                    assertEquals("ÁRBOL", it.getString(1))
                    assertEquals("árbol", it.getString(2))
                    assertEquals(productSearchKey("ÁRBOL"), it.getString(3))
                    assertEquals(12.5, it.getDouble(4), 0.0)
                    assertEquals(-3.5, it.getDouble(5), 0.0)
                    assertEquals("Original", it.getString(6))
                    assertEquals(1L, it.getLong(7))
                    assertEquals("ABC", it.getString(8))
                }
            }
        } finally { context.deleteDatabase(migrationName) }
    }

    @Test fun editsKeepSearchKeysCurrentAndExportsPreserveBusinessFieldsAndOrder() = runBlocking(Dispatchers.IO) {
        val dao = database.productsRoomDao()
        val old = dao.getProductDetailsById(1)
        dao.updateProduct(old.copy(name = "ÁRBOL edited"))
        assertEquals(listOf(1L), repository.getMatchingIds(ProductsQuery(search = "ÁRBOL")))
        assertEquals("árbol edited", dao.getProductListRowById(1)!!.nameKey)
        dao.addStockAndUpdateProduct(StockRoomEntity(productId = 1, description = "Sale", amount = -2.0,
            date = "2026-11-05T00:00:00Z", timeStamp = "1"))
        val legacy = ProductsRepositoryImpl(dao, database.categoriesRoomDao())
        val details = legacy.getProductsWithIds(listOf(1L)).single()
        val exported = legacy.getProductsForExport(listOf(2L, 1L, 2L))
        assertEquals(listOf(2L, 1L, 2L), exported.map { it.id })
        val one = exported[1]
        assertEquals(details.name, one.name)
        assertEquals(details.details, one.details)
        assertEquals(details.category, one.category)
        assertEquals(details.barcode, one.barcode)
        assertEquals(details.stock.quantity, one.stock.quantity, 0.0)
        assertEquals(details.price, one.price)
        assertEquals(1, details.stock.history.size)
        assertTrue(one.stock.history.isEmpty())
        assertEquals(121, legacy.countProducts())
        assertEquals(121, legacy.getAllProducts().first().size)
    }

    @Test fun seekAndPrefixQueriesUseTheirIndexesOnAndroidSqlite() {
        val db = database.openHelper.readableDatabase
        val plans = listOf(
            "SELECT id FROM products_table WHERE (nameKey,id) > ('product',20) ORDER BY nameKey,id LIMIT 40" to "index_products_table_nameKey_id",
            "SELECT id FROM products_table WHERE (price,id) > (1,20) ORDER BY price,id LIMIT 40" to "index_products_table_price_id",
            "SELECT id FROM products_table WHERE (stock,id) > (1,20) ORDER BY stock,id LIMIT 40" to "index_products_table_stock_id",
            "SELECT id FROM products_table WHERE searchName >= 'product 0000' AND searchName < 'product 0001' AND name LIKE 'Product 0000%' LIMIT 40" to "index_products_table_searchName"
        )
        plans.forEach { (sql, index) ->
            val plan = db.query("EXPLAIN QUERY PLAN $sql").use { cursor ->
                buildList { while (cursor.moveToNext()) add(cursor.getString(3)) }.joinToString()
            }
            assertTrue(plan, plan.contains("SEARCH"))
            assertTrue(plan, plan.contains(index))
            assertFalse(plan, plan.contains("TEMP B-TREE"))
        }
    }

    private suspend fun load(source: ProductsPagingSource, key: ProductCursor? = null): PagingSource.LoadResult.Page<ProductCursor, ProductListItem> {
        val result = if (key == null) source.load(PagingSource.LoadParams.Refresh(null, 17, false))
            else source.load(PagingSource.LoadParams.Append(key, 17, false))
        assertTrue(result.toString(), result is PagingSource.LoadResult.Page)
        return result as PagingSource.LoadResult.Page
    }
}
