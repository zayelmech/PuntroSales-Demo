package com.imecatro.domain.products.repository
/**
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit

class ProductsRepositoryDummyImpl(
    private val localDatabase: DummyRepository = DummyRepository
) : ProductsRepository {

//    init {
//        for (i in 1..5) {
//
//            localDatabase.fakeList.add(
//                ProductDomainModel(
//                    i,
//                    "Product Name $i",
//                    3f,
//                    "pz",
//                    ProductUnit.Default,
//                    "This product must contain info here",
//                    null
//
//                )
//
//            )
//        }
//    }

    override fun addProduct(product: ProductDomainModel?) {
        product?.let {p ->
            p.id?.let {
                localDatabase.fakeList.add(p)
            }
            val newId = localDatabase.fakeList.size + 1
            localDatabase.fakeList.add(p.apply {
                id = newId
            })

        }
    }

    override suspend fun getAllProducts(): List<ProductDomainModel> {
        return localDatabase.fakeList.toMutableList()
    }

    override fun deleteProductById(id: Int?) {
        localDatabase.fakeList.removeIf {
            it.id == id
        }
    }

    override fun updateProduct(product: ProductDomainModel?) {
        product?.let { p ->
            val last = localDatabase.fakeList.indexOf(localDatabase.fakeList.find { it.id == p.id })
            localDatabase.fakeList[last]= p
        }
    }

    override fun getProductDetailsById(id: Int?): ProductDomainModel? {
        return localDatabase.fakeList.find { it.id == id }
    }
}

 */