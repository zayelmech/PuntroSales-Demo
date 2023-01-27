package com.imecatro.domain.products.repository

import com.imecatro.domain.products.model.ProductDomainModel

object DummyRepository {

    val fakeList: MutableSet<ProductDomainModel> = mutableSetOf()
}