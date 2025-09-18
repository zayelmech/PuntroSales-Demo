package com.imecatro.products.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imecatro.products.data.model.CategoryRoomEntity
import com.imecatro.products.data.model.ProductRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(cat: CategoryRoomEntity): Long

    @Update
    suspend fun updateCategory(cat: CategoryRoomEntity)

    @Query("SELECT * FROM categories_table ORDER BY name")
    fun getAll(): Flow<List<CategoryRoomEntity>>

    @Query("UPDATE products_table SET category_id = :categoryId WHERE id = :productId")
    suspend fun assignCategory(productId: Long, categoryId: Long)

    @Query("UPDATE products_table SET category_id = NULL WHERE id = :productId")
    suspend fun clearCategory(productId: Long)

    @Query("""
        SELECT p.* FROM products_table p
        WHERE (:categoryId IS NULL AND p.category_id IS NULL)
           OR (:categoryId IS NOT NULL AND p.category_id = :categoryId)
        ORDER BY name
    """)
    suspend fun getProductsByCategory(categoryId: Long?): List<ProductRoomEntity>

    @Query("SELECT * FROM categories_table WHERE name = :name")
    suspend fun getCategoryByName(name: String) : CategoryRoomEntity?
}