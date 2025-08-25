package com.imecatro.demosales.domain.sales.list.usecases

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import kotlinx.coroutines.flow.Flow

class GetAllSalesUseCase(
    private val allSalesRepository: AllSalesRepository
) {
    operator fun invoke(): Flow<List<SaleOnListDomainModel>> = allSalesRepository.getAllSales()
}
