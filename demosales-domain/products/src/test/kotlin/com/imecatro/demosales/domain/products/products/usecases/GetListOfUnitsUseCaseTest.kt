package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.products.usecases.GetListOfUnitsUseCase
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class GetListOfUnitsUseCaseTest {

    @Test
    operator fun invoke() {
        val useCase = GetListOfUnitsUseCase()
        val listSorted = listOf("kg","g","L","ml","m","cm","pz").sorted()
        assertEquals(listSorted,useCase.invoke().sorted())
    }
}