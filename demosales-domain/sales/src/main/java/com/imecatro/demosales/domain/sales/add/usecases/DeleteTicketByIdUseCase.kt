package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository

class DeleteTicketByIdUseCase(
    private val detailsSaleRepository: DetailsSaleRepository
) {

    suspend operator fun invoke(id: Long) {
        detailsSaleRepository.deleteSaleWithId(id)
    }
}
