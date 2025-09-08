package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetCartFlowUseCase(
    private val addSaleRepository: AddSaleRepository,
    private val detailsSaleRepository: DetailsSaleRepository
) {
    suspend operator fun invoke(id: Long? = null): Flow<SaleDomainModel> {
        if (id != null) {
            val sale = detailsSaleRepository.getSaleDetailsById(id)
            if (sale.status != OrderStatus.INITIALIZED) {
                val newId = addSaleRepository.duplicateProductsFromSale(id)
                return addSaleRepository.getCartFlow(newId)
            }
        }
        return addSaleRepository.getCartFlow(id)


    }

}