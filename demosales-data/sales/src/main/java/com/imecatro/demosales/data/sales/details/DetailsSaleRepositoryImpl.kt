package com.imecatro.demosales.data.sales.details

import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DetailsSaleRepositoryImpl(
    private val salesRoomDao: SalesRoomDao,
    private val ordersRoomDao: OrdersRoomDao,
    //TODO clients Dao
) : DetailsSaleRepository {
    override suspend fun getSaleDetailsById(id: Long): SaleDetailsDomainModel =
        withContext(Dispatchers.IO) {

            val list = async { ordersRoomDao.getListOfProductsBySaleId(id) }.await()
            val sale = async { salesRoomDao.getSaleById(id) }.await()

            return@withContext SaleDetailsDomainModel(
                list = list.map {
                    Order(it.id, it.productId, it.productName, it.productPrice, it.qty)
                },
                status = OrderStatus.entries.find { it.str == sale.status }
                    ?: OrderStatus.INITIALIZED,
                clientId = sale.clientId,
                note = sale.note ?: "",
                shippingCost = 0.0,
                tax = 0.0,
                extra = sale.extra,
                total = ordersRoomDao.calculateTotalForSale(id) + sale.extra //TODO
            )
        }

    override suspend fun deleteSaleWithId(id: Long) {
        withContext(Dispatchers.IO) {
            salesRoomDao.deleteSaleWithId(id)
        }
    }
}
