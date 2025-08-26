package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.model.Currencies


class GetListOfCurrenciesUseCase() {
    operator fun invoke(): List<String> {
        val list = Currencies.entries.toTypedArray()
        return list.map { it.code }
    }
}