package com.imecatro.products.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "stock_table",
    foreignKeys = [ForeignKey(
        entity = ProductRoomEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("product_id"),
        onDelete = ForeignKey.CASCADE
    )])
data class StockRoomEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "product_id", index = true)
    val productId : Int,
    val description : String,
    val amount : Float,
    val date : String,
    val timeStamp : String,

)