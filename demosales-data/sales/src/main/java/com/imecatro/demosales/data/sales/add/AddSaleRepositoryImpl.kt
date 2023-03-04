package com.imecatro.demosales.data.sales.add

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import kotlinx.coroutines.flow.Flow

class AddSaleRepositoryImpl(dao: AddSaleDao) : AddSaleRepository {
    override suspend fun createNewSale(sale: SaleModelDomain) {
        TODO("Not yet implemented")
    }

    override suspend fun addProductToCart(order: Order) {
        TODO("Not yet implemented")
    }

    override fun getCartFlow(): Flow<SaleModelDomain> {
        TODO("Not yet implemented")
    }
}