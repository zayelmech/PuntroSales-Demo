package com.imecatro.products.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products_table",
    foreignKeys = [ForeignKey(
        entity = CategoryRoomEntity::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("category_id"), Index(value = ["nameKey", "id"]),
        Index(value = ["price", "id"]), Index(value = ["stock", "id"]),
        Index(value = ["category_id", "nameKey", "id"]), Index("searchName"), Index("barcode")]
)
data class ProductRoomEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    @ColumnInfo(name = "price")
    val price: Double,
    val currency: String,
    val unit: String,
    val stock: Double,
    val details: String,
    val imageUri: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    val barcode : String? = null,
    @ColumnInfo(defaultValue = "''")
    val nameKey: String = name.lowercase(),
    // Keep the original text: legacy LIKE is ASCII-insensitive, not Unicode case folding.
    @ColumnInfo(collate = ColumnInfo.NOCASE, defaultValue = "''")
    val searchName: String = name,
)
