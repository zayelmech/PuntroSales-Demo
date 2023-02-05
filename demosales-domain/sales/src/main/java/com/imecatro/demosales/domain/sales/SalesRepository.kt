package com.imecatro.demosales.domain.sales

import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleModelDomain

interface SalesRepository {
    fun getAllSales(): List<SaleModelDomain>

    //add
    fun createNewSale(sale: SaleModelDomain)

    //details
    fun changeSaleStatus(id: Int, status: OrderStatus)
    fun updateOrder(sale: SaleModelDomain)
    fun deleteOrderById(id: Int)


}