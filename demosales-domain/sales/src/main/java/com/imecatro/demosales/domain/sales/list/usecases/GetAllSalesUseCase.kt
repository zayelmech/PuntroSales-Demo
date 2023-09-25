package com.imecatro.demosales.domain.sales.list.usecases

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepositoryDummy
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class GetAllSalesUseCase(
    private val allSalesRepository: AllSalesRepository
) {
    operator fun invoke(): Flow<List<SaleOnListDomainModel>> = allSalesRepository.getAllSales()
}
