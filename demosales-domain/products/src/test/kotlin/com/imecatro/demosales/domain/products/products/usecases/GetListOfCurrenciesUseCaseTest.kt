package com.imecatro.demosales.domain.products.products.usecases

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetListOfCurrenciesUseCaseTest {


    @Test
    operator fun invoke() {
        val usecase = GetListOfCurrenciesUseCase()

        assertEquals(listOf("USD", "EUR", "MXN"), usecase.invoke())
    }


}