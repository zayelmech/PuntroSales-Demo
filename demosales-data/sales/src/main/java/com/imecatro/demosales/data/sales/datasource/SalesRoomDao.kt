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


    /**
     * Returns a list of sales prepared for the "sales list" screen.
     *
     * For each sale in [sales_table] this query:
     *
     * - Selects the sale's `id` (`s.id`) as `id`.
     * - Joins with [client_table] to get the client name. If the client
     *   does not exist, `COALESCE` ensures an empty string is returned
     *   instead of `NULL`.
     * - Uses the sale's `creationDateMillis` as `date`.
     * - Calculates the total as the sum of all order lines in
     *   [order_table] (`qty * productPrice`) for the given sale.
     *   If no order lines exist, `COALESCE` ensures `0.0` is used.
     *   Then adds the `extra` field from the sale row to that sum.
     * - Selects the sale's `status`.
     *
     * The query performs:
     *
     * - `LEFT JOIN client_table AS c ON c.id = s.clientId`
     *   Includes the client if found; otherwise the sale still appears.
     * - `LEFT JOIN order_table AS o ON o.sale_id = s.id`
     *   Includes all order lines for the sale (or none).
     *
     * The `GROUP BY s.id` ensures aggregation per sale, so the SUM of
     * order lines is calculated per individual sale.
     *
     * The results are ordered by `s.id DESC` (newest sale first).
     *
     * @return A [Flow] that emits the list of [SaleFullTransactionModel],
     *         each containing:
     *         - [SaleOnListDomainModel.id]
     *         - [SaleOnListDomainModel.clientName]
     *         - [SaleOnListDomainModel.date]
     *         - [SaleOnListDomainModel.total]
     *         - [SaleOnListDomainModel.status]
     */
    @Query("""
        SELECT 
            s.id AS id,
            COALESCE(c.name, '') AS clientName,
            s.creationDateMillis AS date,
            COALESCE(SUM(o.qty * o.productPrice), 0.0) + s.extra AS total,
            s.status AS status
        FROM sales_table AS s
        LEFT JOIN client_table AS c ON c.id = s.clientId
        LEFT JOIN order_table AS o ON o.sale_id = s.id
        GROUP BY s.id
        ORDER BY s.id DESC
    """)
    fun getSalesForList(): Flow<List<SaleFullTransactionModel>>
}