package com.imecatro.demosales.data.sales.list.repository

import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last

class AllSalesRepositoryImpl(
    private val salesRoomDao: SalesRoomDao
) : AllSalesRepository {

    private val allSales = salesRoomDao.getAllSales()
    override suspend fun getAllSales(): List<SaleOnListDomainModel> {
       return allSales.last().toDomain()
    }


}

private fun List<SaleDataRoomModel>.toDomain(): List<SaleOnListDomainModel> {
    return map {
        SaleOnListDomainModel(
            id = it.id,
            clientName ="Guest", //TODO add functionality
            date = it.creationDateMillis.toString(), //TODO add functionality
            total = 0.0,
            status = it.status.toOrderStatus()
        )
    }
}

private fun String.toOrderStatus(): OrderStatus {
    return OrderStatus.valueOf(this)
}
