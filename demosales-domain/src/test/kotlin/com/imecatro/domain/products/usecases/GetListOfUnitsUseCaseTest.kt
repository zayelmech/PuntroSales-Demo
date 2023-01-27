package com.imecatro.domain.products.usecases

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