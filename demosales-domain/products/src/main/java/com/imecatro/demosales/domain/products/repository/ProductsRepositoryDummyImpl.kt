package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryDummyImpl(
    private val localDatabase: DummyRepository = DummyRepository
) : ProductsRepository {

    override fun addProduct(product: ProductDomainModel?) {
        product?.let { p ->
            p.id?.let {
                DummyRepository.fakeList.add(p)
            }?:run {
                val newId = DummyRepository.fakeList.size + 1
                DummyRepository.fakeList.add(p.apply {
                    id = newId
                })
            }
        }
    }

    override fun getAllProducts(): Flow<List<ProductDomainModel>> {
        return flow { emit(DummyRepository.fakeList) }
    }

    override fun deleteProductById(id: Int?) {
        DummyRepository.fakeList.removeIf {
            it.id == id
        }
    }

    override fun updateProduct(product: ProductDomainModel?) {
        product?.let { p ->
            val last = DummyRepository.fakeList.indexOf(DummyRepository.fakeList.find { it.id == p.id })
            DummyRepository.fakeList[last] = p
        }
    }

    override fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return DummyRepository.fakeList.find { it.id == id }
    }
}

