package com.imecatro.demosales.products

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.datasource.AppRoomDatabase
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.*
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.data.repository.CategoriesRepositoryImpl
import com.imecatro.products.data.repository.ProductsPagingRepositoryImpl
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import com.imecatro.products.ui.list.views.ListOfProductsStateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class ProductsInventoryUiTest {
    @get:Rule val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "products-ui-${UUID.randomUUID()}.db"
    private val rowQueries = AtomicInteger()
    private val fullCatalogReads = AtomicInteger()
    private val store = ViewModelStore()
    private lateinit var database: AppRoomDatabase
    private lateinit var viewModel: ProductsViewModel

    @Before fun open() {
        database = Room.databaseBuilder(context, AppRoomDatabase::class.java, name)
            .setQueryCallback({ sql, _ -> if (sql.startsWith("SELECT p.id,p.name,p.nameKey")) rowQueries.incrementAndGet() }, java.util.concurrent.Executor { it.run() })
            .build()
        seedProducts(database, 121)
        val legacy = ProductsRepositoryImpl(database.productsRoomDao(), database.categoriesRoomDao())
        val tracked = object : ProductsRepository by legacy {
            override fun getAllProducts() = legacy.getAllProducts().also { fullCatalogReads.incrementAndGet() }
        }
        val dispatchers = object : CoroutineProvider {
            override val io = Dispatchers.IO
            override val main = Dispatchers.Main
        }
        val files = object : FileInteractor {
            override fun writeCsvToTickets(filename: String, rows: List<List<String>>): File =
                File(context.cacheDir, "test-$filename").apply { writeText(rows.joinToString("\n") { it.joinToString(",") }) }
        }
        viewModel = ProductsViewModel(tracked,
            ProductsPagingRepositoryImpl(database, database.productsRoomDao()),
            GetAllCategoriesUseCase(CategoriesRepositoryImpl(database.categoriesRoomDao()), dispatchers),
            ExportProductsCsvUseCase(tracked, files, dispatchers),
            IsCatalogPublishedUseCase(EmptyCatalogRepository, dispatchers))
        store.put("products", viewModel)
    }

    @After fun close() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { store.clear() }
        database.close()
        context.deleteDatabase(name)
    }

    @Test fun selectionCoversUnloadedRowsAndDoesNotReloadPages() {
        compose.setContent { PuntroSalesDemoTheme { ListOfProductsStateImpl(viewModel, {}, onNavigateAction = {}) } }
        awaitCard(1)
        val reads = rowQueries.get()
        compose.onNodeWithTag("ElevatedCard-1").performTouchInput { longClick() }
        compose.waitUntil(5_000) { viewModel.uiState.value.idsSelected == listOf(1L) }
        compose.runOnIdle { assertEquals(reads, rowQueries.get()) }
        compose.runOnIdle { viewModel.onSelectAllProducts(true) }
        compose.waitUntil(5_000) { viewModel.uiState.value.idsSelected.size == 121 }
        assertEquals((1L..121L).toList(), viewModel.uiState.value.idsSelected)
        assertEquals(0, fullCatalogReads.get())
        compose.runOnIdle { viewModel.onClearSelections() }
        compose.waitUntil(5_000) { viewModel.uiState.value.idsSelected.isEmpty() }
    }

    @Test fun returningToCompositionReusesPagesAndStockChangesRemainVisible() {
        val visible = mutableStateOf(true)
        compose.setContent { PuntroSalesDemoTheme { if (visible.value) ListOfProductsStateImpl(viewModel, {}, onNavigateAction = {}) } }
        awaitCard(1)
        val reads = rowQueries.get()
        compose.runOnIdle { visible.value = false }
        compose.runOnIdle { visible.value = true }
        awaitCard(1)
        assertEquals(reads, rowQueries.get())
        runBlocking(Dispatchers.IO) { database.productsRoomDao().updateProductStock(777.0, 1) }
        compose.waitUntil(5_000) { compose.onAllNodesWithText("777.0  pz").fetchSemanticsNodes().isNotEmpty() }
        assertEquals(0, fullCatalogReads.get())
    }

    @Test fun searchThenClearRestoresTheInventory() {
        compose.setContent { PuntroSalesDemoTheme { ListOfProductsStateImpl(viewModel, {}, onNavigateAction = {}) } }
        awaitCard(1)
        compose.onNodeWithContentDescription("Search").performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("Product 000000")
        awaitCard(121)
        compose.onNode(hasSetTextAction()).performTextReplacement("missing")
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("products-no-results").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasSetTextAction()).performTextReplacement("")
        awaitCard(1)
    }

    @Test fun scrollLoadsFurtherPagesWithoutReadingFullCatalog() {
        compose.setContent { PuntroSalesDemoTheme { ListOfProductsStateImpl(viewModel, {}, onNavigateAction = {}) } }
        awaitCard(1)
        compose.onNodeWithTag("products-list").performScrollToIndex(35)
        compose.waitUntil(5_000) { rowQueries.get() >= 2 }
        compose.onNodeWithTag("products-list").performScrollToIndex(60)
        awaitCard(61)
        assertEquals(0, fullCatalogReads.get())
    }

    private fun awaitCard(id: Long) {
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("ElevatedCard-$id").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("ElevatedCard-$id").assertIsDisplayed()
    }
}

private object EmptyCatalogRepository : CatalogRepository {
    override suspend fun getPublishedCatalog(): WebCatalogDomainModel? = null
    override suspend fun isCatalogPublished() = false
    override suspend fun unpublishCatalog() = Unit
    override suspend fun uploadImage(imageUri: String): String = error("Not used by inventory tests")
    override suspend fun uploadJsonCatalog(catalog: WebCatalogDomainModel): String = error("Not used by inventory tests")
    override suspend fun getCatalogWebViewerUrl(): String = error("Not used by inventory tests")
    override suspend fun createsFinalUrl(jsonCatalogUrl: String): String = error("Not used by inventory tests")
    override suspend fun previewUrl(jsonCatalogUrl: String): String = error("Not used by inventory tests")
}
