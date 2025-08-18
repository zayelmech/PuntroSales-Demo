package com.imecatro.demosales.data.sales.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_table")
data class SaleDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Int = 0,
    val creationDateMillis: Long = 0,
    val status: String = "",
    @ColumnInfo(name = "extra")
    val extra: Double = 0.0,
    val note: String = ""
)
