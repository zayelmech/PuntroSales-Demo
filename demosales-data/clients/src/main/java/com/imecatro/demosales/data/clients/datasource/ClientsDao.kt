package com.imecatro.demosales.data.clients.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import com.imecatro.demosales.data.clients.model.PurchaseRoomEntity
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

    @Query("SELECT * FROM client_table WHERE phone = :phoneNumber LIMIT 1")
    fun getClientByPhoneNumber(phoneNumber: String): ClientRoomEntity?


    @Query("SELECT * FROM purchases_table where client_id = :id")
    fun getPurchasesByClientId(id: Long): Flow<List<PurchaseRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPurchase(purchase: PurchaseRoomEntity)

    @Query("DELETE FROM purchases_table WHERE purchaseNumber = :purchaseNumber")
    fun deletePurchaseByNumber(purchaseNumber: String)

    @Query("UPDATE purchases_table SET description = description || '-Cancelled', amount = 0 WHERE purchaseNumber = :purchaseNumber")
    fun cancelPurchaseByNumber(purchaseNumber: String)

    @Query("SELECT client_id FROM purchases_table WHERE purchaseNumber = :purchaseNumber")
    fun getClientIdByPurchaseNumber(purchaseNumber: String): Long

    @Query("UPDATE client_table SET accumulatedPurchases = (SELECT SUM(amount) FROM purchases_table WHERE client_id = :clientId) WHERE id = :clientId")
    fun recalculateAccumulatedPurchases(clientId: Long)

    @Transaction
    fun cancelPurchaseAndRecalculate(purchaseNumber: String) {
        val clientId = getClientIdByPurchaseNumber(purchaseNumber)
        cancelPurchaseByNumber(purchaseNumber)
        recalculateAccumulatedPurchases(clientId)
    }

    @Transaction
    fun addPurchaseAndRecalculate(purchase: PurchaseRoomEntity) {
        addPurchase(purchase)
        recalculateAccumulatedPurchases(purchase.clientId)
    }

    @Query("UPDATE client_table SET isFavorite = :isFavorite WHERE id = :clientId")
    fun updateFavoriteStatus(clientId: Long, isFavorite: Boolean)
}