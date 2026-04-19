package com.imecatro.demosales.data.clients.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchases_table",
    foreignKeys = [ForeignKey(
        entity = ClientRoomEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("client_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PurchaseRoomEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val purchaseNumber: String, // from store is sale, for client it's a purchase
    @ColumnInfo(name = "client_id", index = true)
    val clientId: Long,
    val description: String, // Extra or additional information
    @ColumnInfo(name = "amount")
    val amount: Double, // represent the total from ticket
    val date: Long
)