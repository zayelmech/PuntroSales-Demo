package com.imecatro.domain.products.repository

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit

class ProductsRepositoryDummyImpl(
    private val localDatabase: DummyRepository = DummyRepository
) : ProductsRepository {

    init {
        for (i in 1..5) {

            localDatabase.fakeList.add(
                ProductDomainModel(
                    i,
                    "Product Name $i",
                    3f,
                    "pz",
                    ProductUnit.DEFAULT,
                    "This product must contain info here",
                    null

                )

            )
        }
    }

    override fun addProduct(product: ProductDomainModel?) {
        product?.let {
            localDatabase.fakeList.add(it)
        }
    }

    override suspend fun getAllProducts(): List<ProductDomainModel> {
        return localDatabase.fakeList
    }

    override fun deleteProductById(id: Int?) {
        localDatabase.fakeList.removeIf {
            it.id == id
        }
    }

    override fun updateProduct(product: ProductDomainModel?) {
        //TODO Implement update method
    }
}