package com.imecatro.products.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: ProductRoomEntity): Long


    @Query("SELECT * FROM products_table ORDER BY id")
    fun getAllProducts(): Flow<List<ProductRoomEntity>>

    @Query("DELETE FROM products_table WHERE id = :id")
    fun deleteProductById(id: Long)

    @Update
    fun updateProduct(product: ProductRoomEntity)

    @Query("SELECT * FROM products_table WHERE id = :id ")
    fun getProductDetailsById(id: Long): ProductRoomEntity

    @Query("""
    SELECT * FROM products_table 
    WHERE (:productName != '' AND name LIKE :productName || '%')
""")
    fun searchProducts(productName: String): Flow<List<ProductRoomEntity>>

    @Query("SELECT * FROM stock_table WHERE product_id = :id")
    fun getProductStockHistory(id : Long) : List<StockRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStock(stock: StockRoomEntity)

    @Query("UPDATE products_table SET stock = :newStock WHERE id = :id")
    fun updateProductStock(newStock : Double, id: Long)
}