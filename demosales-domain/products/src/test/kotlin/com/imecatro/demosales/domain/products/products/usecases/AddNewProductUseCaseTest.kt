package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.core.model.ProductUnit
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepositoryDummyImpl
import com.imecatro.demosales.domain.products.usecases.AddNewProductUseCase
import com.imecatro.demosales.domain.products.usecases.State
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddNewProductUseCaseTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Adding new Product to dummy repo`() {
        val fakeRepo = ProductsRepositoryDummyImpl()

        val useCase = AddNewProductUseCase(fakeRepo, mainThreadSurrogate)
        val input = ProductDomainModel(
            id = 99,
            name = "Some",
            currency = "USD",
            price = 1.0,
            unit = ProductUnit.Default.symbol,
            details = "Info here",
            imageUri = "/data/0",
            stock = ProductStockDomainModel(0.0,1.0, emptyList())

        )

        val output = State(1, null)
        runTest {
            useCase.invoke(input)
            assertEquals(input, fakeRepo.getProductDetailsById(99))
        }


    }
}