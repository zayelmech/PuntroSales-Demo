package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleDummyRepoImpl
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue


class AddNewSaleToDatabaseUseCaseTest {

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
    fun `Adding new Sale to dummy repo`() {
        val fakeRepo = AddSaleDummyRepoImpl()

        val useCase = AddNewSaleToDatabaseUseCase(fakeRepo,mainThreadSurrogate)
        val sale = SaleDomainModel(
            id = 0,
            clientId = 1,
            date = "today",
            productsList = listOf(Order(0,"", 2f,2f)),
            total = 2.0,
            status = OrderStatus.COMPLETED
        )

        runTest {
            val response = useCase.invoke(sale)
            assertTrue(response.isSuccess)
            //assertEquals(sale, fakeRepo.sales.first())
        }
        assertEquals(sale, fakeRepo.sales.first())

    }

}