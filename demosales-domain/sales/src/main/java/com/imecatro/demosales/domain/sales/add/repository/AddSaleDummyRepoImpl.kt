package com.imecatro.demosales.domain.sales.add.repository

import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import kotlinx.coroutines.flow.*

class AddSaleDummyRepoImpl : AddSaleRepository {

    //val sales = mutableListOf<SaleModelDomain>()

    private val cartList = mutableListOf<Order>()

    private val currentTicket = SaleModelDomain(
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

    private val ticketOnCache: MutableSharedFlow<SaleModelDomain> = MutableStateFlow(currentTicket)

    override suspend fun createNewSale(sale: SaleModelDomain) {
//        sales.add(sale)
    }

    override suspend fun addProductToCart(order: Order) {
//        productsOnCart.emit(cartList.apply { add(order) })
        ticketOnCache.emit(currentTicket.apply { productsList = cartList.apply { add(order) } })
    }

    override fun getCartFlow(): Flow<SaleModelDomain> {
        return ticketOnCache
    }


}


