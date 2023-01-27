package com.imecatro.domain.products.usecases

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test


class GetListOfCurrenciesUseCaseTest {


    @Test
    operator fun invoke() {
        val usecase = GetListOfCurrenciesUseCase()

        assertEquals( listOf("USD","EUR","MXN"),usecase.invoke())
    }
}