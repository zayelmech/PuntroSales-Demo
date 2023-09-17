package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.Flow

class GetCartFlowUseCase(
    private val addSaleRepository: AddSaleRepository
) {
    operator fun invoke() : Flow<SaleDomainModel> = addSaleRepository.getCartFlow()
}