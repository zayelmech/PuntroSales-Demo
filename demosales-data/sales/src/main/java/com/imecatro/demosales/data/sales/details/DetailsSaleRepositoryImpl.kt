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
                clientId = sale.clientId?:0,
                note = sale.note,
                discount = sale.totals?.discount?:0.0,
                extra = sale.totals?.extra?:0.0,
                total = sale.totals?.total?:0.0,
            )
        }

    override suspend fun updateSaleStatusWithId(id: Long, status: OrderStatus) {
        withContext(Dispatchers.IO) {
            salesRoomDao.updateSaleStatus(id, status.str)
        }
    }

    override suspend fun deleteSaleWithId(id: Long) {
        withContext(Dispatchers.IO) {
            salesRoomDao.deleteSaleWithId(id)
        }
    }
}
