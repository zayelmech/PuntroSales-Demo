package com.imecatro.demosales.data.sales.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_table")
data class SaleDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val clientId: Int,
    val creationDateMillis: Long,
    val status: String
)

@Entity(tableName = "order_table")
data class OrderDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val saleId: Int,
    val ticketOrder: Int,
    val productId: Int,
    val qty: Float
)
