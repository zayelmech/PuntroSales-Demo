package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.SaleModelDomain

 class AddSaleDummyRepoImpl : AddSaleRepository {
    val sales = mutableListOf<SaleModelDomain>()
    override suspend fun createNewSale(sale: SaleModelDomain) {
        sales.add(sale)
    }

}