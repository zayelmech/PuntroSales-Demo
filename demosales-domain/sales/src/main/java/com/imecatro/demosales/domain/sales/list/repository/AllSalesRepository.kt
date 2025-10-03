package com.imecatro.demosales.domain.sales.list.repository

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.Flow

interface AllSalesRepository {
    //list
    fun getAllSales(): Flow<List<SaleOnListDomainModel>>

    suspend fun getSalesWithIds(ids : List<Long>) : List<SaleDomainModel>
}