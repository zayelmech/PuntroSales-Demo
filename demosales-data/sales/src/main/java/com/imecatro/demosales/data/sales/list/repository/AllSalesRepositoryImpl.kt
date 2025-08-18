package com.imecatro.demosales.data.sales.list.repository

import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.list.mappers.toDomain
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.measureTimedValue

class AllSalesRepositoryImpl(
    private val salesRoomDao: SalesRoomDao,
    private val ordersRoomDao : OrdersRoomDao
) : AllSalesRepository {

    private val allSales = salesRoomDao.getAllSales()
    override fun getAllSales(): Flow<List<SaleOnListDomainModel>> =
        allSales.map { list ->
            list.map { saleFromDb ->
                val total = ordersRoomDao.calculateTotalForSale(saleFromDb.id) + saleFromDb.extra
                saleFromDb.toDomain(total)
            }
        }.flowOn(Dispatchers.IO)
}
