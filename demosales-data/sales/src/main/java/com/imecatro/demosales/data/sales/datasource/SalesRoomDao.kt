package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.demosales.data.sales.model.SaleFullTransactionModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesRoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSaleOnLocalDatabase(sale: SaleDataRoomModel) : Long

    @Update
    suspend fun saveSaleState(sale: SaleDataRoomModel)

    @Query("SELECT * FROM sales_table ORDER BY id DESC")
    fun getAllSales(): Flow<List<SaleDataRoomModel>>

    @Query("SELECT * FROM sales_table WHERE id = :id")
    fun getFlowSaleById(id : Long): Flow<SaleDataRoomModel>

    @Query("SELECT * FROM sales_table WHERE id = :id")
    suspend fun getSaleById(id : Long): SaleDataRoomModel

    @Query("DELETE FROM sales_table WHERE id = :id")
    suspend fun deleteSaleWithId(id : Long)

    @Query("UPDATE sales_table SET status = :status WHERE id = :id")
    suspend fun updateSaleStatus(id : Long, status : String)

}