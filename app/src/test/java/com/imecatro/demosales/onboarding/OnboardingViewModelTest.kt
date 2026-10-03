package com.imecatro.demosales.onboarding

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetAllProductsUseCase
import com.imecatro.demosales.domain.products.usecases.IsCatalogPublishedUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = object : CoroutineProvider {
        override val io = dispatcher
        override val main = dispatcher
    }
    private var persisted = OnboardingProgress()
    private val store = OnboardingStore(object : OnboardingPersistence {
        override fun read() = persisted
        override fun write(progress: OnboardingProgress) { persisted = progress }
    }, dispatchers)
    private val products = MutableStateFlow<List<ProductDomainModel>>(emptyList())
    private val sales = MutableStateFlow<List<SaleOnListDomainModel>>(emptyList())
    private var details = SaleDetailsDomainModel(emptyList(), status = OrderStatus.PENDING)
    private var failDetails = false

    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun model() = OnboardingViewModel(
        store,
        GetAllProductsUseCase(stub<ProductsRepository> { products }, dispatcher),
        GetAllSalesUseCase(stub<AllSalesRepository> { sales }),
        GetDetailsOfSaleByIdUseCase(stub<DetailsSaleRepository> { if (failDetails) error("unavailable") else details }),
        IsCatalogPublishedUseCase(stub<CatalogRepository> { null }, dispatchers),
        dispatchers
    )

    @Test fun `recognizes existing products and saved order and keeps milestones after deletion`() = runTest(dispatcher) {
        products.value = (1L..5L).map { id -> ProductDomainModel(id, "A", 1.0, "USD", stock = ProductStockDomainModel(1.0, 0.0, emptyList()), details = "", imageUri = null, category = null, barcode = null) }
        sales.value = listOf(SaleOnListDomainModel(1, "", 0, 1.0, OrderStatus.PENDING))
        details = details.copy(list = listOf(Order(1, 1, "A", 1.0, 1.0)))
        val vm = model()
        advanceUntilIdle()
        assertEquals(setOf(OnboardingStep.PRODUCTS, OnboardingStep.ORDER), vm.progress.value.steps)
        assertEquals(5, vm.productIds.value.size)
        products.value = emptyList()
        sales.value = emptyList()
        advanceUntilIdle()
        assertEquals(2, vm.progress.value.steps.size)
    }

    @Test fun `errors and empty orders never count as saved orders`() = runTest(dispatcher) {
        sales.value = listOf(SaleOnListDomainModel(1, "", 0, 1.0, OrderStatus.PENDING))
        val vm = model()
        advanceUntilIdle()
        assertFalse(OnboardingStep.ORDER in vm.progress.value.steps)
        failDetails = true
        sales.value = listOf(SaleOnListDomainModel(2, "", 0, 1.0, OrderStatus.COMPLETED))
        advanceUntilIdle()
        assertFalse(OnboardingStep.ORDER in vm.progress.value.steps)
    }

    @Test fun `dismiss stays dismissed on data updates and a fresh viewmodel`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        assertTrue(vm.showIntroduction.value)
        vm.dismissIntroduction()
        vm.planLoaded(false, true, false)
        advanceUntilIdle()
        assertFalse(vm.showIntroduction.value)
        assertTrue(vm.progress.value.steps.isEmpty())
        val restarted = model()
        advanceUntilIdle()
        assertFalse(restarted.showIntroduction.value)
    }

    @Test fun `successful plan records a milestone but loading and failure do not`() = runTest(dispatcher) {
        val vm = model()
        vm.planLoaded(true, false, false)
        vm.planLoaded(false, true, false)
        advanceUntilIdle()
        assertTrue(vm.progress.value.steps.isEmpty())
        vm.planLoaded(false, false, false)
        advanceUntilIdle()
        assertTrue(OnboardingStep.FULFILLMENT in vm.progress.value.steps)
    }
}
