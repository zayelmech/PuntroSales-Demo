package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.ResultDomainCodes
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddNewSaleToDatabaseUseCase(
    private val addSaleRepository: AddSaleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(saleModelDomain: SaleDomainModel): Result<ResultDomainCodes> =
        withContext(dispatcher) {
            return@withContext try {
                addSaleRepository.saveSale(saleModelDomain)
                Result.success(ResultDomainCodes.SUCCESS)
            } catch (e: Exception) {
                Result.failure(Throwable(e))
            }
        }
}


