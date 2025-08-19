package com.imecatro.demosales.data.clients.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ClientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addClient(client: ClientRoomEntity)

    @Query("SELECT * FROM client_table ORDER BY id")
    fun getAllClients(): Flow<List<ClientRoomEntity>>

    @Query("DELETE FROM client_table WHERE id = :id")
    fun deleteClientById(id: Long)

    @Update
    fun updateClient(product: ClientRoomEntity)

    @Query("SELECT * FROM client_table WHERE id = :id ")
    fun getClientDetailsById(id: Long): ClientRoomEntity

    @Query("SELECT * FROM client_table WHERE name LIKE :clientName || '%'")
    fun searchClients(clientName: String): Flow<List<ClientRoomEntity>>

}