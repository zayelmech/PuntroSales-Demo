package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.ResultDomainCodes
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AddNewSaleToDatabaseUseCase(
    private val addSaleRepository: AddSaleRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(saleModelDomain: SaleModelDomain): Result<ResultDomainCodes> =
        withContext(dispatcher) {
            return@withContext try {
                addSaleRepository.createNewSale(saleModelDomain)
                Result.success(ResultDomainCodes.SUCCESS)
            } catch (e: Exception) {
                Result.failure(Throwable(e))
            }
        }
}


