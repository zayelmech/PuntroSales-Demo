package com.imecatro.demosales.domain.sales.details

class GetDetailsOfSaleByIdUseCase(
    private val detailsSaleRepository: DetailsSaleRepository
) {
    suspend operator fun invoke(id: Long): SaleDetailsDomainModel =
        detailsSaleRepository.getSaleDetailsById(id)

}

