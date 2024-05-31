package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order


class UpdateProductOnCartUseCase(
    private val addSaleRepository: AddSaleRepository
) {
    suspend operator fun invoke(order: Order) =
        addSaleRepository.updateProductQtyOnCart(order)

}
