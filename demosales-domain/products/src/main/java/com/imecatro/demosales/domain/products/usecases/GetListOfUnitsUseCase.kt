package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.products.model.ProductUnit

class GetListOfUnitsUseCase() {
    operator fun invoke(): List<String> {

        val list = ProductUnit::class.nestedClasses.map { it.objectInstance as ProductUnit }
        return list.map { it.symbol }
    }
}