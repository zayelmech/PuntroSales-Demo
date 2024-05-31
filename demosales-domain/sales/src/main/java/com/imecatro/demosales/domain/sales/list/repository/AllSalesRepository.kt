package com.imecatro.demosales.domain.sales.list.repository

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface AllSalesRepository {
    //list
    fun getAllSales(): Flow<List<SaleOnListDomainModel>>

}

class AllSalesRepositoryDummy : AllSalesRepository {
    override fun getAllSales(): Flow<List<SaleOnListDomainModel>> {
        return flowOf(
            listOf(
                SaleOnListDomainModel(
                    id = 2,
                    "Abdiel",
                    1,
                    200.0,
                    OrderStatus.PENDING
                )
            )
        )
    }

}
