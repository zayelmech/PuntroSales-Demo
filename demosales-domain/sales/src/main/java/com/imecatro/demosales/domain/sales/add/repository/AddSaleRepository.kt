package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import kotlinx.coroutines.flow.Flow


interface AddSaleRepository {
    suspend fun createNewSale(sale: SaleModelDomain)
   suspend fun addProductToCart(order: Order)

   fun getCartFlow(): Flow<SaleModelDomain>
}