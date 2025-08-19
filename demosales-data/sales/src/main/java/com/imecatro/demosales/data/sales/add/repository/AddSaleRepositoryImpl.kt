package com.imecatro.demosales.data.sales.add.repository

import android.util.Log
import com.imecatro.demosales.data.sales.add.mappers.toData
import com.imecatro.demosales.data.sales.add.mappers.toDataSource
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.list.mappers.toOrderStatus
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.Order
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
        val id = salesRoomDao.saveSaleState(sale = sale.toData(ticketId ?: 0))
        Log.d(TAG, "createNewSale: $id")
    }

    override suspend fun addProductToCart(order: Order) {
        this.ticketId?.let { id ->
            ordersRoomDao.saveOrder(order.toDataSource(id))
        } ?: run {
            Log.e(TAG, "addProductToCart: ", Throwable("Missing ticket id $ticketId"))
        }
    }

    override suspend fun updateProductQtyOnCart(order: Order) {
        withContext(Dispatchers.IO) {
            ordersRoomDao.updateOrderQty(order.id, order.qty)
        }
    }

    override suspend fun deleteProductOnCart(id: Long) {
        withContext(Dispatchers.IO) {
            ordersRoomDao.deleteOrderById(id)
        }
    }


    override suspend fun getCartFlow(saleId: Long?): Flow<SaleDomainModel> {
        val id: Long =
            saleId
                ?: withContext(Dispatchers.IO) { salesRoomDao.insertSaleOnLocalDatabase(sale = SaleDataRoomModel()) }
        this.ticketId = id

        val itemsList = ordersRoomDao.getListOfProductsOnSaleWithId(id)
        val saleFlow = salesRoomDao.getFlowSaleById(id)

        return combine(itemsList, saleFlow) { products, sale ->
            val total = ordersRoomDao.calculateTotalForSale(id)
            SaleDomainModel(
                id = sale.id,
                clientId = sale.clientId,
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
                    subTotal = total,
                    extraCost = sale.extra,
                    total = (total + sale.extra)
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
