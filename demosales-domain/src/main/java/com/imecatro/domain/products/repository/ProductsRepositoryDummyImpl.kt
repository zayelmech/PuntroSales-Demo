package com.imecatro.domain.products.repository

import com.imecatro.domain.products.model.ProductDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryDummyImpl(
    private val localDatabase: DummyRepository = DummyRepository
) : ProductsRepository {

    override fun addProduct(product: ProductDomainModel?) {
        product?.let { p ->
            p.id?.let {
                localDatabase.fakeList.add(p)
            }?:run {
                val newId = localDatabase.fakeList.size + 1
                localDatabase.fakeList.add(p.apply {
                    id = newId
                })
            }
        }
    }

    override fun getAllProducts(): Flow<List<ProductDomainModel>> {
        return flow { emit(localDatabase.fakeList) }
    }

    override fun deleteProductById(id: Int?) {
        localDatabase.fakeList.removeIf {
            it.id == id
        }
    }

    override fun updateProduct(product: ProductDomainModel?) {
        product?.let { p ->
            val last = localDatabase.fakeList.indexOf(localDatabase.fakeList.find { it.id == p.id })
            localDatabase.fakeList[last] = p
        }
    }

    override fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return localDatabase.fakeList.find { it.id == id }
    }
}

