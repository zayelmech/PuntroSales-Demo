package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesRoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSaleOnLocalDatabase(sale: SaleDataRoomModel) : Long

    @Query("SELECT * FROM sales_table ORDER BY id")
    fun getAllSales(): Flow<List<SaleDataRoomModel>>

    @Query("SELECT * FROM sales_table WHERE id = :id")
    fun getFlowSaleById(id : Long): Flow<SaleDataRoomModel>

    @Query("SELECT * FROM sales_table WHERE id = :id")
    suspend fun getSaleById(id : Long): SaleDataRoomModel
}