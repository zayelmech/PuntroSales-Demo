package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.Flow


interface AddSaleRepository {
    suspend fun createNewSale(sale: SaleDomainModel)
   suspend fun addProductToCart(order: Order)

   fun getCartFlow(): Flow<SaleDomainModel>
}