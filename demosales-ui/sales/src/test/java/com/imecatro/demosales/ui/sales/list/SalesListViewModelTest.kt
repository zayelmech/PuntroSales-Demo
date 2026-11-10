package com.imecatro.demosales.ui.sales.list

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.usecases.*
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.list.model.SalesDateRange
import com.imecatro.demosales.ui.sales.list.model.StatusFilterUiModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SalesListViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val day = LocalDate.of(2026, 11, 10)
    private fun sale(id: Long, date: LocalDate, status: OrderStatus = OrderStatus.COMPLETED) =
        SaleOnListDomainModel(id, "Client", date.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), 10.0, status)
    private val sales = MutableStateFlow(listOf(
        sale(1, day.minusDays(1)), sale(2, day), sale(3, day, OrderStatus.PENDING), sale(4, day.plusDays(1))
    ))
    private val repository = object : AllSalesRepository {
        override fun getAllSales() = sales
        override suspend fun getSalesWithIds(ids: List<Long>): List<SaleDomainModel> = error("Unused")
    }
    private val details = object : DetailsSaleRepository {
        override suspend fun getSaleDetailsById(id: Long): SaleDetailsDomainModel = error("Unused")
        override suspend fun updateSaleStatusWithId(id: Long, status: OrderStatus): Unit = error("Unused")
        override suspend fun deleteSaleWithId(id: Long): Unit = error("Unused")
    }
    private val files = object : FileInteractor {
        override fun writeCsvToTickets(filename: String, rows: List<List<String>>): File = error("Unused")
    }

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun TestScope.observe(): SalesListViewModel {
        val coroutines = object : CoroutineProvider {
            override val io = dispatcher
            override val main = dispatcher
        }
        val vm = SalesListViewModel(GetAllSalesUseCase(repository), coroutines,
            ExportSalesReportUseCase(repository, files, coroutines),
            ExportProductsFromSaleUseCase(details, files, coroutines), GetSalesMetricsUseCase(repository))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.salesListUiState.collect() }
        advanceUntilIdle()
        return vm
    }

    @Test fun `all dates is default and clearing date restores full list`() = runTest(dispatcher) {
        val vm = observe()
        assertEquals(listOf(1L, 2L, 3L, 4L), vm.salesListUiState.value.map { it.id })
        vm.onDateFilterChange(SalesDateRange(day))
        advanceUntilIdle()
        assertEquals(listOf(2L, 3L), vm.salesListUiState.value.map { it.id })
        vm.onDateFilterChange(null)
        advanceUntilIdle()
        assertEquals(4, vm.salesListUiState.value.size)
    }

    @Test fun `date status and ID search intersect and retain other filters on clear`() = runTest(dispatcher) {
        val vm = observe()
        vm.onDateFilterChange(SalesDateRange(day, day.plusDays(1)))
        vm.onStatusFilterChange(vm.statusFilterState.value.first { it.type == StatusFilterUiModel.Type.COMPLETED })
        advanceUntilIdle()
        assertEquals(listOf(2L, 4L), vm.salesListUiState.value.map { it.id })
        vm.onSearchQueryChange("2")
        advanceUntilIdle()
        assertEquals(listOf(2L), vm.salesListUiState.value.map { it.id })
        vm.onDateFilterChange(null)
        advanceUntilIdle()
        assertEquals(listOf(2L), vm.salesListUiState.value.map { it.id })
        vm.onSearchQueryChange("")
        advanceUntilIdle()
        assertEquals(listOf(1L, 2L, 4L), vm.salesListUiState.value.map { it.id })
    }

    @Test fun `new sales are evaluated against active date and empty results recover`() = runTest(dispatcher) {
        val vm = observe()
        vm.onDateFilterChange(SalesDateRange(day.plusDays(5)))
        advanceUntilIdle()
        assertTrue(vm.salesListUiState.value.isEmpty())
        sales.value += sale(5, day.plusDays(5))
        advanceUntilIdle()
        assertEquals(listOf(5L), vm.salesListUiState.value.map { it.id })
    }

    @Test fun `changing dates clears hidden export selection and select all uses visible rows`() = runTest(dispatcher) {
        val vm = observe()
        vm.onSelectAllSales(true)
        advanceUntilIdle()
        assertTrue(vm.reportState.value.allSelected)
        vm.onDateFilterChange(SalesDateRange(day))
        advanceUntilIdle()
        assertTrue(vm.reportState.value.ids.isEmpty())
        assertFalse(vm.reportState.value.allSelected)
        assertFalse(vm.reportState.value.enableSelection)
        vm.onSelectAllSales(true)
        advanceUntilIdle()
        assertEquals(listOf(2L, 3L), vm.reportState.value.ids)
        vm.onDateFilterChange(null)
        advanceUntilIdle()
        assertTrue(vm.reportState.value.ids.isEmpty())
    }

    @Test fun `list date filter leaves today total and chart metrics independent`() = runTest(dispatcher) {
        sales.value += sale(99, LocalDate.now())
        val vm = observe()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.todayTotalAmount.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.metrics.collect() }
        advanceUntilIdle()
        val total = vm.todayTotalAmount.value
        val metrics = vm.metrics.value
        assertTrue(total > 0)
        assertTrue(metrics.today.isNotEmpty())
        vm.onDateFilterChange(SalesDateRange(day.minusYears(5)))
        advanceUntilIdle()
        assertTrue(vm.salesListUiState.value.isEmpty())
        assertEquals(total, vm.todayTotalAmount.value, 0.0)
        assertEquals(metrics, vm.metrics.value)
    }

    @Test fun `reapplying same range preserves selection`() = runTest(dispatcher) {
        val vm = observe()
        vm.onDateFilterChange(SalesDateRange(day))
        advanceUntilIdle()
        vm.onCardSelected(2)
        vm.onDateFilterChange(SalesDateRange(day))
        advanceUntilIdle()
        assertEquals(listOf(2L), vm.reportState.value.ids)
    }
}
