package com.imecatro.demosales.data.sales.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel

@Dao
interface SalesRoomDao {

    @Insert
    fun insertSaleOnLocalDatabase(sale: SaleDataRoomModel)


}