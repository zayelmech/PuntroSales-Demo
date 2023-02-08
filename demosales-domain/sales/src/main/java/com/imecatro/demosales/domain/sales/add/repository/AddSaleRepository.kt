package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.SaleModelDomain


interface AddSaleRepository {
    suspend fun createNewSale(sale: SaleModelDomain)
}