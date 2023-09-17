package com.imecatro.demosales.domain.sales.update

import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel

interface UpdateSaleRepository {

    fun changeSaleStatus(id: Int, status: OrderStatus)
    fun updateOder(sale: SaleDomainModel)
    fun deleteOrderById(id: Int)
}