package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.core.model.ProductUnit
import com.imecatro.demosales.domain.products.model.ProductDomainModel

object DummyRepository {

    val fakeList: MutableList<ProductDomainModel> = mutableListOf()
    init {
        for (i in 1..5) {

            fakeList.add(
                ProductDomainModel(
                    i,
                    "Product Name $i",
                    3f,
                    "pz",
                    ProductUnit.Default.symbol,
                    "This product must contain info here",
                    null

                )

            )
        }
    }
}