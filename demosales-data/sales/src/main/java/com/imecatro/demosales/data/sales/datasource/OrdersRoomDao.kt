package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOrder(order : OrderDataRoomModel)

    @Update
    suspend fun updateOrder(order: OrderDataRoomModel)
    @Query("UPDATE order_table SET qty = :newValue WHERE id = :id")
    suspend fun updateOrderQty(id: Long, newValue: Double)  // Adjust the data type as needed

    @Query("SELECT * FROM order_table WHERE id = :id")
    fun getOrderById(id: Int) : OrderDataRoomModel

    @Query("SELECT * FROM order_table WHERE sale_id = :id")
    suspend fun getListOfProductsBySaleId(id : Long) : List<OrderDataRoomModel>

    @Query("SELECT * FROM order_table WHERE sale_id = :id")
    fun getListOfProductsOnSaleWithId(id : Long) : Flow<List<OrderDataRoomModel>>

    @Query("SELECT SUM(qty * productPrice) FROM order_table WHERE sale_id = :saleId")
    fun calculateTotalForSale(saleId: Long) : Double
    @Query("DELETE FROM order_table WHERE id = :id")
    suspend fun deleteOrderById(id: Long)

    @Query("SELECT productId FROM order_table GROUP BY productId ORDER BY COUNT(productId) DESC LIMIT :n")
    suspend fun getMostPopularProducts(n : Int) : List<Int>

    @Query("""
    SELECT SUM(qty * productPrice) 
    FROM order_table 
    WHERE sale_id = :saleId
""")
    suspend fun getSaleAmount(saleId: Long): Float?
}