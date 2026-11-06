package com.imecatro.products.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.imecatro.products.data.model.CategoryRoomEntity
import com.imecatro.products.data.model.ProductListRow
import com.imecatro.products.data.paging.productSearchKey
import androidx.room.Transaction
import androidx.room.Update
import com.imecatro.products.data.model.ProductAnCategoryDetailsRoomModel
import com.imecatro.products.data.model.ProductFullDetailsRoomModel
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @RawQuery
    suspend fun getProductListRows(query: SupportSQLiteQuery): List<ProductListRow>

    @Query("SELECT p.id,p.name,p.nameKey,p.price,p.unit,p.stock,p.imageUri,c.name AS category FROM products_table p LEFT JOIN categories_table c ON c.id=p.category_id WHERE p.id=:id")
    suspend fun getProductListRowById(id: Long): ProductListRow?

    @RawQuery
    suspend fun getProductListIds(query: SupportSQLiteQuery): List<Long>

    @RawQuery(observedEntities = [ProductRoomEntity::class, CategoryRoomEntity::class])
    fun observeProductListCount(query: SupportSQLiteQuery): Flow<Int>

    @Query("SELECT COUNT(*) FROM products_table")
    suspend fun countProducts(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: ProductRoomEntity): Long

    fun addProduct(product: ProductRoomEntity): Long = insertProduct(
        product.copy(nameKey = product.name.lowercase(), searchName = productSearchKey(product.name))
    )


    @Deprecated("Use [getProductsFullDetailsByd] instead")
    @Query("SELECT * FROM products_table ORDER BY id")
    fun getAllProducts(): Flow<List<ProductRoomEntity>>

    @Transaction
    @Query("SELECT * FROM products_table ORDER BY name")
    fun getProductsWithCategories(): Flow<List<ProductAnCategoryDetailsRoomModel>>

    @Transaction
    @Query("SELECT * FROM products_table WHERE id IN (:ids)")
    fun getProductsForExport(ids: List<Long>): List<ProductAnCategoryDetailsRoomModel>

    @Transaction
    @Query("SELECT * FROM products_table WHERE id = :id ORDER BY name")
    fun getProductFullDetailsByd(id: Long): ProductFullDetailsRoomModel

    @Query("DELETE FROM products_table WHERE id = :id")
    fun deleteProductById(id: Long)

    @Update
    fun persistProduct(product: ProductRoomEntity)

    fun updateProduct(product: ProductRoomEntity) = persistProduct(
        product.copy(nameKey = product.name.lowercase(), searchName = productSearchKey(product.name))
    )

    @Query("SELECT * FROM products_table WHERE id = :id ")
    fun getProductDetailsById(id: Long): ProductRoomEntity

    @Query(
        """
    SELECT * FROM products_table 
    WHERE (:productName != '' AND name LIKE :productName || '%')
"""
    )
    fun searchProducts(productName: String): Flow<List<ProductRoomEntity>>

    @Query("""SELECT * FROM products_table WHERE (:barcode != '' AND barcode LIKE :barcode)""")
    suspend fun searchProductByBarcode(barcode: String): ProductRoomEntity

    @Query("SELECT * FROM stock_table WHERE product_id = :id ORDER BY id DESC")
    fun getProductStockHistory(id: Long): List<StockRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStock(stock: StockRoomEntity)

    @Query("UPDATE products_table SET stock = :newStock WHERE id = :id")
    fun updateProductStock(newStock: Double, id: Long)

    @Query("UPDATE products_table SET stock = stock + :delta WHERE id = :productId")
    suspend fun bumpProductStock(productId: Long, delta: Double)

    // --- API pública: UNA sola transacción ---
    @Transaction
    suspend fun addStockAndUpdateProduct(stock: StockRoomEntity) {
        // 1) Inserta el movimiento de inventario (positivo o negativo)
        addStock(stock)
        // 2) Ajusta el stock acumulado del producto
        bumpProductStock(stock.productId, stock.amount)
    }


    /**
     * Recalculates and updates the stock of a product based on its stock history.
     * This function should be used when there's a need to ensure the product's stock
     * accurately reflects the sum of all its stock movements.
     *
     * It queries the `stock_table` for all entries related to the given `productId`,
     * sums their `amount` values, and then updates the `stock` field in the
     * `products_table` for that product. If there are no stock entries for the product,
     * the stock will be set to 0.
     *
     * @param productId The ID of the product whose stock needs to be rebuilt.
     */
    @Query("""UPDATE products_table SET stock = IFNULL((SELECT SUM(s.amount) FROM stock_table s WHERE s.product_id = :productId), 0) WHERE id = :productId""")
    suspend fun rebuildProductStock(productId: Long)
}
