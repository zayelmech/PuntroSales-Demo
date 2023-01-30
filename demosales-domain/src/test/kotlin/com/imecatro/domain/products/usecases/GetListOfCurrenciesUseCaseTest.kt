package com.imecatro.domain.products.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test


class GetListOfCurrenciesUseCaseTest {


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    operator fun invoke() {
        val usecase = GetListOfCurrenciesUseCase()
        val dispatcher = StandardTestDispatcher()

        assertEquals( listOf("USD","EUR","MXN"),usecase.invoke())
    }



}