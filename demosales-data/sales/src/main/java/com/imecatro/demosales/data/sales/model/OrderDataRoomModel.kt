package com.imecatro.demosales.data.sales.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "order_table",
    foreignKeys = [ForeignKey(
        entity = SaleDataRoomModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("sale_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class OrderDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "sale_id", index = true)
    val saleId: Long,
    val productId: Int,
    val productName: String,
    val productPrice: Float,
    val qty: Float,
    val productImage : String
)

