package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetCartFlowUseCase(
    private val addSaleRepository: AddSaleRepository
) {
    suspend operator fun invoke(id: Long? = null): Flow<SaleDomainModel> =
        addSaleRepository.getCartFlow(id)
}