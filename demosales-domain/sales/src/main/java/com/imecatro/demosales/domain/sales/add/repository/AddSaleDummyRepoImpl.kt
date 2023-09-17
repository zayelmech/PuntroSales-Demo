package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.*

class AddSaleDummyRepoImpl : AddSaleRepository {

    val sales = mutableListOf<SaleDomainModel>()

    private val cartList = mutableListOf<Order>()

    private val currentTicket = SaleDomainModel(
        id = 0,
        clientId = 0,
        date = "",
        productsList = listOf(),
        total = 0.0,
        status = OrderStatus.INITIALIZED
    )

//    private val productsOnCart: MutableSharedFlow<List<Order>> =
//        MutableStateFlow<List<Order>>(listOf()).apply {
//            onEach {
//                cartList.clear()
//                cartList.addAll(it)
//
//            }
//        }

    private val ticketOnCache: MutableSharedFlow<SaleDomainModel> = MutableStateFlow(currentTicket)

    override suspend fun createNewSale(sale: SaleDomainModel) {
        sales.add(sale)
    }

    override suspend fun addProductToCart(order: Order) {
//        productsOnCart.emit(cartList.apply { add(order) })
        ticketOnCache.emit(currentTicket.apply { productsList = cartList.apply { add(order) } })
    }

    override fun getCartFlow(): Flow<SaleDomainModel> {
        return ticketOnCache
    }


}


