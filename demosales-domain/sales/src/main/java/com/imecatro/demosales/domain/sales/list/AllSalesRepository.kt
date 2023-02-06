package com.imecatro.demosales.domain.sales.list

import com.imecatro.demosales.domain.sales.model.SaleModelDomain

interface AllSalesRepository {
    //list
    suspend fun getAllSales(): List<SaleModelDomain>

}