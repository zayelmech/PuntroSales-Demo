package com.imecatro.products.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_table")
data class ProductRoomEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val price: Float,
    val currency: String,
    val unit: String,
    val stock: Double,
    val details: String,
    val imageUri: String

)
