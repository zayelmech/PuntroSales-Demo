package com.imecatro.demosales.domain.sales.list.usecases

import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepositoryDummy
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetAllSalesUseCase(
    private val allSalesRepository: AllSalesRepository = AllSalesRepositoryDummy(),
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<List<SaleOnListDomainModel>> = withContext(dispatcher) {
        try {
            val response = allSalesRepository.getAllSales()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
