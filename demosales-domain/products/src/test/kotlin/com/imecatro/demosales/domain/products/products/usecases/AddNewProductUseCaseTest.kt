package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.core.model.ProductUnit
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.AddNewProductUseCase
import com.imecatro.demosales.domain.products.usecases.State
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddNewProductUseCaseTest {
    @Test fun `adding a product preserves all business fields`() = runTest {
        val repository = TestProductsRepository()
        val useCase = AddNewProductUseCase(repository, StandardTestDispatcher(testScheduler))
        val input = ProductDomainModel(id = 99, name = "Some", currency = "USD", price = 1.0,
            unit = ProductUnit.Default.symbol, details = "Info here", imageUri = "/data/0",
            stock = ProductStockDomainModel(0.0, 1.0, emptyList()), category = null, barcode = null)
        assertEquals(State(1, null), useCase(input))
        assertEquals(input, repository.getProductDetailsById(99))
    }

    @Test fun `failed persistence returns the existing error state`() = runTest {
        val repository = TestProductsRepository().apply { failure = IllegalStateException("Unavailable") }
        val useCase = AddNewProductUseCase(repository, StandardTestDispatcher(testScheduler))
        val product = ProductDomainModel(id = 1, name = "Some", currency = "USD", price = 1.0,
            unit = "pz", details = "", imageUri = null, stock = ProductStockDomainModel(0.0, 0.0, emptyList()),
            category = null, barcode = null)
        assertEquals(State(null, "Unavailable"), useCase(product))
        assertEquals(emptyList<ProductDomainModel>(), repository.products)
    }
}

/** Fixture belongs only to the JVM test source set. */
private class TestProductsRepository : ProductsRepository {
    val products = mutableListOf<ProductDomainModel>()
    var failure: Exception? = null
    override suspend fun addProduct(product: ProductDomainModel) { failure?.let { throw it }; products += product }
    override fun getAllProducts() = flowOf(products.toList())
    override suspend fun getProductDetailsById(id: Long) = products.firstOrNull { it.id == id }
    override fun getProductsWithIds(ids: List<Long>) = products.filter { it.id in ids }
    override suspend fun deleteProductById(id: Long) { products.removeAll { it.id == id } }
    override suspend fun updateProduct(product: ProductDomainModel) = error("Not used")
    override fun searchProducts(letter: String) = flowOf(products.filter { it.name?.startsWith(letter) == true })
    override suspend fun searchProductByBarcode(barcode: String) = products.firstOrNull { it.barcode == barcode }
    override suspend fun addStock(reference: String, productId: Long, amount: Double) = error("Not used")
    override suspend fun removeStock(reference: String, productId: Long, amount: Double) = error("Not used")
}
