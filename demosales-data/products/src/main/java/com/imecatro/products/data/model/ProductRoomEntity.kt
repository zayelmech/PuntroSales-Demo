package com.imecatro.products.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_table")
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
    val imageUri: String

)
