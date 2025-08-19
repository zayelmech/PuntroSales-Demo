package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.Flow


interface AddSaleRepository {
    suspend fun saveSale(sale: SaleDomainModel)
    suspend fun addProductToCart(order: Order)

    suspend fun updateProductQtyOnCart(order: Order)
    suspend fun deleteProductOnCart(id: Long)
    suspend fun getCartFlow(saleId: Long?): Flow<SaleDomainModel>


    /**
     * Filter popular products
     * Will search the [n] most popular products from the order table if there any
     *
     * @param n is the number of items needed in the list
     * @return a list of id's [Int]
     */
    suspend fun filterPopularProducts(n: Int): List<Long>
}