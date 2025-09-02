package com.imecatro.demosales.data.sales.list.repository

import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.list.mappers.toDomain
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AllSalesRepositoryImpl(
    salesRoomDao: SalesRoomDao,
) : AllSalesRepository {

    private val allSales = salesRoomDao.getAllSales()

    override fun getAllSales(): Flow<List<SaleOnListDomainModel>> =
        allSales.map { lst -> lst.map { it.toDomain() } }.flowOn(Dispatchers.IO)
}