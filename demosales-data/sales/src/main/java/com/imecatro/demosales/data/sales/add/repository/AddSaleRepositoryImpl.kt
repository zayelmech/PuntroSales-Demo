package com.imecatro.demosales.data.sales.add.repository

import com.imecatro.demosales.data.sales.add.mappers.toData
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import kotlinx.coroutines.flow.Flow

class AddSaleRepositoryImpl(
    private val salesRoomDao: SalesRoomDao
) : AddSaleRepository {
    override suspend fun createNewSale(sale: SaleModelDomain) {
        salesRoomDao.insertSaleOnLocalDatabase(sale = sale.toData())
    }

    override suspend fun addProductToCart(order: Order) {
        TODO("Not yet implemented")
    }

    override fun getCartFlow(): Flow<SaleModelDomain> {
        TODO("Not yet implemented")
    }
}
