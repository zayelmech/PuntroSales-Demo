package com.imecatro.demosales.data.sales.add.repository

import android.util.Log
import com.imecatro.demosales.data.sales.add.mappers.toData
import com.imecatro.demosales.data.sales.add.mappers.toDataSource
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.list.mappers.toOrderStatus
import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
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
        val id = salesRoomDao.insertSaleOnLocalDatabase(sale = sale.toData(ticketId ?: 0))
        Log.d(TAG, "createNewSale: $id")
    }

    override suspend fun addProductToCart(order: Order) {
        this.ticketId?.let { id ->
            ordersRoomDao.saveOrder(order.toDataSource(id))
        } ?: run {
            Log.e(TAG, "addProductToCart: ", Throwable("Missing ticket id $ticketId"))
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
            val total = calculateTotal(products)
            SaleDomainModel(
                id = sale.id,
                clientId = sale.clientId,
                date = sale.creationDateMillis.toString(),//TODO
                productsList = products.map {
                    Order(
                        productId =  it.productId,
                        productName = it.productName,
                        productPrice = it.productPrice,
                        qty = it.qty)
                },
                total = total,
                status = sale.status.toOrderStatus()
            )
        }.flowOn(Dispatchers.IO)
    }

    private fun calculateTotal(products: List<OrderDataRoomModel>): Double {
        return products.sumOf { (it.productPrice * it.qty).toDouble() }
    }
}
