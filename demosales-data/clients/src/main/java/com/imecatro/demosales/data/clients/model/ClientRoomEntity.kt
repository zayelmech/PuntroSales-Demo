package com.imecatro.demosales.data.clients.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_table")
data class ClientRoomEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String,
    val phone: String,
    val address: String,
    val imageUri: String,
    //useful
    val timestamp: Long,
    val tableVersion: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accumulatedPurchases: Double = 0.0, // The sum of all totals' in purchases made by the client
    val isFavorite : Boolean = false,  // If the user selected as favorite
)