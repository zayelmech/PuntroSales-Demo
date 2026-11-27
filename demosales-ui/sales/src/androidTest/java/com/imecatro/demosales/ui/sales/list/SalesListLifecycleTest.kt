package com.imecatro.demosales.ui.sales.list

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.usecases.ExportProductsFromSaleUseCase
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetSalesMetricsUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel
import com.imecatro.demosales.ui.sales.list.views.SalesListComposableStateImpl
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import com.imecatro.demosales.ui.theme.common.Money
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SalesListLifecycleTest {
    @get:Rule val compose = createComposeRule()

    @Test fun stopsDatabaseCollectionWhenStoppedAndCollectsMetricsOnlyWhileGraphIsOpen() {
        val activeCollections = AtomicInteger()
        val repository = object : AllSalesRepository {
            override fun getAllSales() = flow {
                activeCollections.incrementAndGet()
                try {
                    emit(listOf(SaleOnListDomainModel(1, "Client", System.currentTimeMillis(), 10.0, OrderStatus.COMPLETED)))
                    awaitCancellation()
                } finally {
                    activeCollections.decrementAndGet()
                }
            }
            override suspend fun getSalesWithIds(ids: List<Long>): List<SaleDomainModel> = error("Unused")
        }
        val details = object : DetailsSaleRepository {
            override suspend fun getSaleDetailsById(id: Long): SaleDetailsDomainModel = error("Unused")
            override suspend fun updateSaleStatusWithId(id: Long, status: OrderStatus): Unit = error("Unused")
            override suspend fun deleteSaleWithId(id: Long): Unit = error("Unused")
        }
        val files = object : FileInteractor {
            override fun writeCsvToTickets(filename: String, rows: List<List<String>>): File = error("Unused")
        }
        val coroutines = object : CoroutineProvider {
            override val io = Dispatchers.IO
            override val main = Dispatchers.Main
        }
        val vm = SalesListViewModel(
            GetAllSalesUseCase(repository), coroutines,
            ExportSalesReportUseCase(repository, files, coroutines),
            ExportProductsFromSaleUseCase(details, files, coroutines), GetSalesMetricsUseCase(repository)
        )
        val store = ViewModelStore().apply { put("sales", vm) }
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry(this)
        }
        compose.runOnUiThread { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        try {
            compose.setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides owner, LocalCurrencyCode provides "USD") {
                    MaterialTheme { SalesListComposableStateImpl(salesListViewModel = vm, onNavigate = {}) }
                }
            }
            compose.waitUntil(5_000) { activeCollections.get() == 2 }
            compose.waitForIdle()
            assertEquals(2, activeCollections.get())

            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val totalText = Money.format("10.0", context.resources.configuration.locales[0], "USD")
            compose.onAllNodesWithText(totalText).onFirst().performClick()
            compose.waitUntil(5_000) { activeCollections.get() == 3 }
            compose.onNodeWithText("Sales overview").assertExists()
            compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
                .onFirst().performSemanticsAction(SemanticsActions.Dismiss) { it() }
            compose.waitUntil(5_000) { activeCollections.get() == 2 }

            compose.runOnUiThread { owner.lifecycle.currentState = Lifecycle.State.CREATED }
            compose.waitUntil(5_000) { activeCollections.get() == 0 }
            compose.runOnUiThread { owner.lifecycle.currentState = Lifecycle.State.STARTED }
            compose.waitUntil(5_000) { activeCollections.get() == 2 }
        } finally {
            compose.runOnUiThread {
                owner.lifecycle.currentState = Lifecycle.State.DESTROYED
                store.clear()
            }
        }
    }
}
