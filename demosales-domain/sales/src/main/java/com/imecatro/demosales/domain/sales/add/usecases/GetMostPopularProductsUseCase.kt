package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository

class GetMostPopularProductsUseCase(
    private val addSaleRepository: AddSaleRepository
) {
    suspend operator fun invoke() : List<Long>{
       return addSaleRepository.filterPopularProducts(10)
    }
}