package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.Currencies

class GetListOfCurrenciesUseCase() {
    operator fun invoke(): List<String> {
        val list = Currencies::class.nestedClasses.map { it.objectInstance as Currencies }
        return list.map {it.code }
    }
}