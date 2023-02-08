package com.imecatro.demosales.datasource.room

import androidx.room.*
import com.imecatro.demosales.datasource.room.entities.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: ProductRoomEntity)


    @Query("SELECT * FROM products_table ORDER BY id")
    fun getAllProducts(): Flow<List<ProductRoomEntity>>

    @Query("DELETE FROM products_table WHERE id = :id")
    fun deleteProductById(id: Int)

    @Update
    fun updateProduct(product: ProductRoomEntity)

    @Query("SELECT * FROM products_table WHERE id = :id ")
    fun getProductDetailsById(id: Int): ProductRoomEntity

    @Query("SELECT * FROM products_table WHERE name LIKE :productName")
    fun searchProducts(productName: String): Flow<List<ProductRoomEntity>>
}