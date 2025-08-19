package com.imecatro.demosales.data.clients.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

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
    val tableVersion: String
)
