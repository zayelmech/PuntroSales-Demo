package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.products.usecases.GetListOfCurrenciesUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetListOfCurrenciesUseCaseTest {


    @Test
    operator fun invoke() {
        val usecase = GetListOfCurrenciesUseCase()

        assertEquals(listOf("USD", "EUR", "MXN").sorted(), usecase.invoke().sorted())
    }


}