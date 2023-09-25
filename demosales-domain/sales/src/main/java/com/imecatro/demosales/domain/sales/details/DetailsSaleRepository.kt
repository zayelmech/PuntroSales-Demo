package com.imecatro.demosales.domain.sales.details

interface DetailsSaleRepository {
    //details
    suspend fun getSaleDetailsById(id: Long) : SaleDetailsDomainModel

}


