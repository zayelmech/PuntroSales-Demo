package com.imecatro.domain.products.repository

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit

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
                    ProductUnit.Default,
                    "This product must contain info here",
                    null

                )

            )
        }
    }
}