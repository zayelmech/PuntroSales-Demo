package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersRoomDao {

    @Insert
    fun saveOrder(order : OrderDataRoomModel)

    @Query("SELECT * FROM order_table WHERE id = :id")
    fun getOrderById(id: Int) : OrderDataRoomModel

    @Query("SELECT * FROM order_table WHERE sale_id = :id")
    suspend fun getListOfProductsBySaleId(id : Long) : List<OrderDataRoomModel>

    @Query("SELECT * FROM order_table WHERE sale_id = :id")
    fun getListOfProductsOnSaleWithId(id : Long) : Flow<List<OrderDataRoomModel>>

    @Query("SELECT SUM(qty * productPrice) FROM order_table WHERE sale_id = :saleId")
    fun calculateTotalForSale(saleId: Long) : Double
}