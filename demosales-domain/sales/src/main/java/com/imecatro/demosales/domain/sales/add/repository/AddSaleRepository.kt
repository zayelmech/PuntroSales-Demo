package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.Flow


interface AddSaleRepository {
    suspend fun saveSale(sale: SaleDomainModel)
    suspend fun updateSaleStatus(id: Long, status: OrderStatus)
    suspend fun addProductToCart(order: Order)

    suspend fun updateProductQtyOnCart(order: Order)
    suspend fun deleteProductOnCart(id: Long)
    suspend fun getCartFlow(saleId: Long?): Flow<SaleDomainModel>

    suspend fun duplicateProductsFromSale(saleId: Long) : Long

    suspend fun updateClientOnSale(sale : SaleDomainModel.Client, saleId : Long)

    /**
     * Filter popular products
     * Will search the [n] most popular products from the order table if there any
     *
     * @param n is the number of items needed in the list
     * @return a list of id's [Int]
     */
    suspend fun filterPopularProducts(n: Int): List<Long>
}