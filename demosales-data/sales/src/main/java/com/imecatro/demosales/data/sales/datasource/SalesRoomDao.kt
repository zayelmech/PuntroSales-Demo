package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesRoomDao {

    @Insert
    fun insertSaleOnLocalDatabase(sale: SaleDataRoomModel)

    @Query("SELECT * FROM sales_table ORDER BY id")
    fun getAllSales(): Flow<List<SaleDataRoomModel>>

}