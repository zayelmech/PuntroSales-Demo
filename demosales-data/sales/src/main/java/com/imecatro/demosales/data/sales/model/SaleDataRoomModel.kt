package com.imecatro.demosales.data.sales.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_table")
data class SaleDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Int = 0,
    val creationDateMillis: Long = 0,
    val status: String = ""
)
