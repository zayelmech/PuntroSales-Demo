package com.imecatro.demosales.data.sales.add.repository

import android.util.Log
import com.imecatro.demosales.data.sales.add.mappers.toData
import com.imecatro.demosales.data.sales.add.mappers.toDataSource
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.list.mappers.toOrderStatus
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.data.sales.model.SaleTotals
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

private const val TAG = "AddSaleRepositoryImpl"

class AddSaleRepositoryImpl(
    private val salesRoomDao: SalesRoomDao,
    private val ordersRoomDao: OrdersRoomDao
) : AddSaleRepository {

    private var ticketId: Long? = null
    override suspend fun saveSale(sale: SaleDomainModel) {
        salesRoomDao.saveSaleState(sale = sale.toData(ticketId ?: 0))
        ticketId?.let { updateSubtotal(it) }
    }

    override suspend fun updateSaleStatus(id: Long, status: OrderStatus) {
        salesRoomDao.updateSaleStatus(id, status.str)
    }

    override suspend fun addProductToCart(order: Order) {
        this.ticketId?.let { id ->
            ordersRoomDao.saveOrder(order.toDataSource(id))
            updateSubtotal(id)
        } ?: run {
            Log.e(TAG, "addProductToCart: ", Throwable("Missing ticket id $ticketId"))
        }
    }

    override suspend fun updateProductQtyOnCart(order: Order) {
        withContext(Dispatchers.IO) {
            ordersRoomDao.updateOrderQty(order.id, order.qty)
            ticketId?.let { updateSubtotal(it) }
        }
    }

    override suspend fun deleteProductOnCart(id: Long) {
        withContext(Dispatchers.IO) {
            ordersRoomDao.deleteOrderById(id)
            ticketId?.let { updateSubtotal(it) }
        }
    }

    private suspend fun updateSubtotal(id: Long) {
        val subtotal = ordersRoomDao.calculateTotalForSale(id)
        val currentSale = salesRoomDao.getSaleById(id)

        val discount = currentSale.totals?.discount ?: 0.0
        val extra = currentSale.totals?.extra ?: 0.0
        val total = subtotal + extra - discount

        val newSale = currentSale.copy(
            totals = currentSale.totals?.copy(subtotal = subtotal, total = total)?: SaleTotals(subtotal = subtotal, total = total)
        )
        salesRoomDao.saveSaleState(newSale)
    }

    override suspend fun getCartFlow(saleId: Long?): Flow<SaleDomainModel> {
        val id: Long =
            saleId
                ?: withContext(Dispatchers.IO) { salesRoomDao.insertSaleOnLocalDatabase(sale = SaleDataRoomModel(creationDateMillis = System.currentTimeMillis())) }
        this.ticketId = id

        val itemsList = ordersRoomDao.getListOfProductsOnSaleWithId(id)
        val saleFlow = salesRoomDao.getFlowSaleById(id)

        return combine(itemsList, saleFlow) { products, sale ->
            SaleDomainModel(
                id = sale.id,
                clientId = sale.clientId ?: 0L,
                date = sale.creationDateMillis.toString(),//TODO
                productsList = products.map {
                    Order(
                        id = it.id,
                        productId = it.productId,
                        productName = it.productName,
                        productPrice = it.productPrice,
                        qty = it.qty
                    )
                },
                totals = SaleDomainModel.Costs(
                    subTotal = sale.totals?.subtotal ?: 0.0,
                    discount = sale.totals?.discount ?: 0.0,
                    extraCost = sale.totals?.extra ?: 0.0,
                    total = sale.totals?.total ?: 0.0,
                ),
                status = sale.status.toOrderStatus()
            )
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun filterPopularProducts(n: Int): List<Long> {
        return withContext(Dispatchers.IO) {
            ordersRoomDao.getMostPopularProducts(n)
        }
    }

}
