package com.imecatro.demosales.data.sales.details

import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.model.Order
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

            val list = async { ordersRoomDao.getListOfProductsBySaleId(id) }
            val sale = salesRoomDao.getSaleById(id)

            return@withContext SaleDetailsDomainModel(
                list = list.await().map {
                    Order(it.productId, it.productName, it.productPrice, it.qty)
                },
                clientName = "UNKNOWN",
                note = "...",
                shippingCost = 0.0,
                tax = 0.0,
                extra = 0.0,
                total = ordersRoomDao.calculateTotalForSale(id) + 0 //TODO
            )
        }

    override suspend fun deleteSaleWithId(id: Long) {
        withContext(Dispatchers.IO) {
            salesRoomDao.deleteSaleWithId(id)
        }
    }
}
