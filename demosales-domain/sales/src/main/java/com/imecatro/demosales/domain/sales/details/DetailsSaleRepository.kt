package com.imecatro.demosales.domain.sales.details

import com.imecatro.demosales.domain.sales.model.OrderStatus

interface DetailsSaleRepository {
    //details
    suspend fun getSaleDetailsById(id: Long) : SaleDetailsDomainModel

    suspend fun updateSaleStatusWithId(id: Long, status : OrderStatus)

    suspend fun deleteSaleWithId(id: Long)

}


