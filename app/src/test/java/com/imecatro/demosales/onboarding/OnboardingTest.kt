package com.imecatro.demosales.onboarding

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy

class OnboardingTest {
    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = object : CoroutineProvider {
        override val io = dispatcher
        override val main = dispatcher
    }
    private class Memory(var value: OnboardingProgress = OnboardingProgress()) : OnboardingPersistence {
        override fun read() = value
        override fun write(progress: OnboardingProgress) { value = progress }
    }
    private fun store(memory: Memory = Memory()) = OnboardingStore(memory, dispatchers)

    @Test fun `absent version presents on clean install and upgrade`() {
        assertTrue(OnboardingProgress().needsPresentation)
        assertTrue(OnboardingProgress(steps = setOf(OnboardingStep.BUSINESS)).needsPresentation)
    }

    @Test fun `claim survives process restart and app version changes`() = runTest(dispatcher) {
        val disk = Memory()
        assertTrue(store(disk).claimPresentation())
        assertFalse(store(disk).claimPresentation())
        assertEquals(ONBOARDING_VERSION, disk.value.presentedVersion)
        assertFalse(OnboardingProgress(presentedVersion = ONBOARDING_VERSION + 1).needsPresentation)
    }

    @Test fun `concurrent claims show presentation only once`() = runTest(dispatcher) {
        val store = store()
        val results = List(5) { async { store.claimPresentation() } }.map { it.await() }
        assertEquals(1, results.count { it })
    }

    @Test fun `presentation and later do not earn progress`() = runTest(dispatcher) {
        val store = store()
        store.claimPresentation()
        assertTrue(store.progress.value.steps.isEmpty())
        assertFalse(store.progress.value.completed)
    }

    @Test fun `legacy defaults and empty names do not count`() {
        listOf(null, "", "   ", "Puntro Sales", " puntro sales ").forEach {
            assertFalse(isConfirmedLegacyBusiness(it))
        }
        assertTrue(isConfirmedLegacyBusiness("Abarrotes Ana"))
    }

    @Test fun `five products count and deletion never revokes milestone`() = runTest(dispatcher) {
        val disk = Memory()
        val store = store(disk)
        store.productsSaved(3)
        assertEquals(3, store.progress.value.productCount)
        assertFalse(OnboardingStep.PRODUCTS in store.progress.value.steps)
        store.productsSaved(5)
        store.productsSaved(0)
        assertTrue(OnboardingStep.PRODUCTS in store(disk).progress.value.steps)
    }

    @Test fun `each step persists independently and any order is allowed`() = runTest(dispatcher) {
        val disk = Memory()
        val store = store(disk)
        OnboardingStep.entries.reversed().forEachIndexed { index, step ->
            store.complete(step)
            assertEquals(index + 1, store(disk).progress.value.steps.size)
        }
        assertTrue(store(disk).progress.value.completed)
        assertNull(store.progress.value.nextStep)
        assertFalse(store.progress.value.completionAcknowledged)
        store.acknowledgeCompletion()
        assertTrue(store(disk).progress.value.completionAcknowledged)
    }

    @Test fun `duplicate events cannot advance other steps`() = runTest(dispatcher) {
        val store = store()
        repeat(5) { store.complete(OnboardingStep.ORDER) }
        assertEquals(1, store.progress.value.steps.size)
        assertEquals(OnboardingStep.BUSINESS, store.progress.value.nextStep)
    }

    @Test fun `only saved nonempty orders qualify`() {
        OrderStatus.entries.forEach { assertFalse(isSavedOrder(it, false)) }
        assertFalse(isSavedOrder(OrderStatus.INITIALIZED, true))
        assertFalse(isSavedOrder(OrderStatus.CANCEL, true))
        assertTrue(isSavedOrder(OrderStatus.PENDING, true))
        assertTrue(isSavedOrder(OrderStatus.COMPLETED, true))
    }

    @Test fun `successful empty or covered plans qualify but errors and incomplete data do not`() {
        assertTrue(isSuccessfulPlan(false, false, false))
        assertFalse(isSuccessfulPlan(true, false, false))
        assertFalse(isSuccessfulPlan(false, true, false))
        assertFalse(isSuccessfulPlan(false, false, true))
    }

    @Test fun `failed checkout does not earn milestone but successful save does`() = runTest(dispatcher) {
        val store = store()
        var fails = true
        val delegate = stub<AddSaleRepository> { name ->
            check(name == "saveSale")
            if (fails) error("Database unavailable") else Unit
        }
        val repo = OnboardingSalesRepository(delegate, store)
        val sale = SaleDomainModel(1, 0, date = "0", productsList = listOf(Order(1, 1, "A", 1.0, 1.0)), status = OrderStatus.PENDING)
        try { repo.saveSale(sale); fail() } catch (_: IllegalStateException) { }
        assertTrue(store.progress.value.steps.isEmpty())
        fails = false
        repo.saveSale(sale)
        assertTrue(OnboardingStep.ORDER in store.progress.value.steps)
        repo.saveSale(sale.copy(status = OrderStatus.CANCEL))
        assertTrue(OnboardingStep.ORDER in store.progress.value.steps)
    }

    @Test fun `catalog preview and failed publication do not count`() = runTest(dispatcher) {
        val store = store()
        var published = false
        val delegate = stub<CatalogRepository> { name ->
            when (name) {
                "previewUrl" -> "https://example.com/preview"
                "createsFinalUrl" -> if (published) "https://example.com/public" else error("Offline")
                "isCatalogPublished" -> published
                else -> error(name)
            }
        }
        val repo = OnboardingCatalogRepository(delegate, store)
        repo.previewUrl("json")
        assertFalse(repo.isCatalogPublished())
        try { repo.createsFinalUrl("json"); fail() } catch (_: IllegalStateException) { }
        assertTrue(store.progress.value.steps.isEmpty())
        published = true
        repo.createsFinalUrl("json")
        assertTrue(OnboardingStep.CATALOG in store.progress.value.steps)
    }

    @Test fun `existing published catalog is recognized`() = runTest(dispatcher) {
        val store = store()
        val repo = OnboardingCatalogRepository(stub { true }, store)
        assertTrue(repo.isCatalogPublished())
        assertTrue(OnboardingStep.CATALOG in store.progress.value.steps)
    }
}

internal inline fun <reified T> stub(crossinline call: (String) -> Any?): T =
    Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ -> call(method.name) } as T
