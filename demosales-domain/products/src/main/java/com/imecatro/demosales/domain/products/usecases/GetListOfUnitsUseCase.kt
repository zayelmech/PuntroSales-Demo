package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.ProductUnit

class GetListOfUnitsUseCase() {
    operator fun invoke(): List<String> {

        val list: Array<ProductUnit> = ProductUnit.values()
        return list.map { it.symbol }
    }
}