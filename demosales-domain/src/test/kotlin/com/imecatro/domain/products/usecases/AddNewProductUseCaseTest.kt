package com.imecatro.domain.products.usecases

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit
import com.imecatro.domain.products.repository.ProductsRepositoryDummyImpl
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals


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
    fun `Adding new Product to dummy repo`(){
        val fakeRepo = ProductsRepositoryDummyImpl()

        val useCase = AddNewProductUseCase(fakeRepo, mainThreadSurrogate)
        val input = ProductDomainModel(
            id = 99,
            name = "Some",
            currency = "USD",
            price = 1f,
            unit = ProductUnit.Default,
            details = "Info here",
            imageUri = "/data/0"

        )

        val output = State(1, null)
        runTest {
            useCase.invoke(input)
            assertEquals(input,fakeRepo.getProductDetailsById(99))
        }


    }
}