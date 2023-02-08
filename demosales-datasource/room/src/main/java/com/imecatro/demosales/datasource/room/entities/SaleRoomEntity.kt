package com.imecatro.demosales.datasource.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_table")
data class SaleRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
//list
    val double: Double,
    val status: String
)