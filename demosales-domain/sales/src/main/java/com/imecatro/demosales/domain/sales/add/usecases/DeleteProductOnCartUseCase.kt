package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository

class DeleteProductOnCartUseCase(
    private val addSaleRepository: AddSaleRepository
) {
    suspend operator fun invoke(id: Long) =
        addSaleRepository.deleteProductOnCart(id)

}
