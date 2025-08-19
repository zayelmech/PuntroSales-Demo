package com.imecatro.demosales.domain.sales.details

import com.imecatro.demosales.domain.sales.model.OrderStatus

class UpdateSaleStatusUseCase(
    private val detailsSaleRepository: DetailsSaleRepository
) {

    suspend operator fun invoke(id: Long, status: OrderStatus) {
        detailsSaleRepository.updateSaleStatusWithId(id, status = status)
    }
}