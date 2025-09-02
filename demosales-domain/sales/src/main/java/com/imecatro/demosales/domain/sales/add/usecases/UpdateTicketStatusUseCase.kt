package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.ResultDomainCodes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateTicketStatusUseCase(
    private val addSaleRepository: AddSaleRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(id : Long, status: OrderStatus): Result<ResultDomainCodes> =
        withContext(dispatcher) {
            return@withContext try {
                addSaleRepository.updateSaleStatus(id,status)
                Result.success(ResultDomainCodes.SUCCESS)
            } catch (e: Exception) {
                Result.failure(Throwable(e))
            }
        }
}

