package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.model.ProductUnit

class GetListOfUnitsUseCase() {
    operator fun invoke(): List<String> {

        val list: Array<ProductUnit> = ProductUnit.entries.toTypedArray()
        return list.map { it.symbol }
    }
}