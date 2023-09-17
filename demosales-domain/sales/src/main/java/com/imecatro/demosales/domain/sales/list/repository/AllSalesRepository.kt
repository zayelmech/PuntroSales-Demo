package com.imecatro.demosales.domain.sales.list.repository

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.OrderStatus

interface AllSalesRepository {
    //list
    suspend fun getAllSales(): List<SaleOnListDomainModel>

}

class AllSalesRepositoryDummy : AllSalesRepository {
    override suspend fun getAllSales(): List<SaleOnListDomainModel> {
        return listOf(SaleOnListDomainModel(id = 2, "Abdiel", "05/12/2023", 200.0, OrderStatus.PENDING))
    }

}
