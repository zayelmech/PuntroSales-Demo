package com.imecatro.demosales.domain.sales.details

class DeleteTicketByIdUseCase(
    private val detailsSaleRepository: DetailsSaleRepository
) {

    suspend operator fun invoke(id: Long) {
        detailsSaleRepository.deleteSaleWithId(id)
    }
}
