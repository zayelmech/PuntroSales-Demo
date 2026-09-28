package com.imecatro.demosales.ui.sales.fulfillment

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetAllProductsUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.fulfillment.viewmodel.FulfillmentPlanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FulfillmentPlanViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val sales = FakeSalesRepository()
    private val products = FakeProductsRepository()
    private val details = FakeDetailsRepository()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private fun TestScope.observePlan(): FulfillmentPlanViewModel {
        val viewModel = FulfillmentPlanViewModel(
            GetAllSalesUseCase(sales),
            GetAllProductsUseCase(products, dispatcher),
            GetDetailsOfSaleByIdUseCase(details),
            object : CoroutineProvider {
                override val io = dispatcher
                override val main = dispatcher
            }
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun `inventory updates recalculate the shopping list`() = runTest(dispatcher) {
        val viewModel = observePlan()
        assertEquals(2.0, viewModel.uiState.value.shoppingList.single().missing!!.toDouble(), 0.0)

        products.values.value = listOf(product(1, 0.0))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shoppingList.isEmpty())
        assertEquals(5.0, viewModel.uiState.value.products.single().available!!.toDouble(), 0.0)
    }

    @Test
    fun `cancelled orders disappear from demand`() = runTest(dispatcher) {
        val viewModel = observePlan()
        sales.values.value = listOf(sales.values.value.single().copy(status = OrderStatus.CANCEL))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.products.isEmpty())
        assertEquals(0, viewModel.uiState.value.orderCount)
        assertFalse(viewModel.uiState.value.canShare)
    }

    @Test
    fun `failure discards partial data and retry recovers`() = runTest(dispatcher) {
        details.fail = true
        val viewModel = observePlan()
        assertTrue(viewModel.uiState.value.hasError)
        assertFalse(viewModel.uiState.value.canShare)

        details.fail = false
        viewModel.refresh()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasError)
        assertTrue(viewModel.uiState.value.canShare)
    }

    @Test
    fun `refresh reloads order details when returning to the plan`() = runTest(dispatcher) {
        val viewModel = observePlan()
        details.value = details.value.copy(list = listOf(order(1, 8.0)))
        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(8.0, viewModel.uiState.value.products.single().demand.toDouble(), 0.0)
    }
}

private class FakeSalesRepository : AllSalesRepository {
    val values = MutableStateFlow(listOf(sale(1, listOf(order(1, 5.0))).sale))
    override fun getAllSales() = values
    override suspend fun getSalesWithIds(ids: List<Long>): List<SaleDomainModel> = error("Unused")
}

private class FakeDetailsRepository : DetailsSaleRepository {
    var value = sale(1, listOf(order(1, 5.0))).details
    var fail = false
    override suspend fun getSaleDetailsById(id: Long): SaleDetailsDomainModel {
        check(!fail) { "Read failed" }
        return value
    }
    override suspend fun updateSaleStatusWithId(id: Long, status: OrderStatus): Unit = error("Read-only plan")
    override suspend fun deleteSaleWithId(id: Long): Unit = error("Read-only plan")
}

private class FakeProductsRepository : ProductsRepository {
    val values = MutableStateFlow(listOf(product(1, -2.0)))
    override fun getAllProducts() = values
    override suspend fun addProduct(product: ProductDomainModel): Unit = error("Read-only plan")
    override suspend fun deleteProductById(id: Long): Unit = error("Read-only plan")
    override suspend fun updateProduct(product: ProductDomainModel): Unit = error("Read-only plan")
    override suspend fun getProductDetailsById(id: Long): ProductDomainModel? = error("Unused")
    override fun searchProducts(letter: String) = error("Unused")
    override suspend fun searchProductByBarcode(barcode: String): ProductDomainModel? = error("Unused")
    override suspend fun addStock(reference: String, productId: Long, amount: Double): Unit = error("Read-only plan")
    override suspend fun removeStock(reference: String, productId: Long, amount: Double): Unit = error("Read-only plan")
    override fun getProductsWithIds(ids: List<Long>): List<ProductDomainModel> = error("Unused")
}
