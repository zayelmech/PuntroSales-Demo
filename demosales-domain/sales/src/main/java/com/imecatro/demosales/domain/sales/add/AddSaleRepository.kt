package com.imecatro.demosales.domain.sales.add

import com.imecatro.demosales.domain.sales.model.SaleModelDomain


interface AddSaleRepository {
    fun createNewSale(sale: SaleModelDomain)
}