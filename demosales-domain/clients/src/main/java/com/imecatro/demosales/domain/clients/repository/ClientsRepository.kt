package com.imecatro.demosales.domain.clients.repository

import com.imecatro.demosales.domain.clients.exception.ClientNotFoundException
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing client data and their associated purchases.
 */
interface ClientsRepository {
    fun addClient(client: ClientDomainModel)
    fun getAllClients(): Flow<List<ClientDomainModel>>
    fun getAllFilteredClients(): List<ClientDomainModel>
    fun deleteClientById(id: Long)
    fun updateClient(client: ClientDomainModel)
    @Throws(ClientNotFoundException::class)
    fun getClientDetailsById(id: Long): ClientDomainModel
    fun searchClient(letter: String): Flow<List<ClientDomainModel>>
    fun getClientByPhoneNumber(phoneNumber: String): ClientDomainModel?

    fun getPurchasesByClientId(id: Long): Flow<List<PurchaseDomainModel>>
    fun addPurchase(purchase: PurchaseDomainModel)
    fun cancelPurchaseByNumber(purchaseNumber: String)
    fun updateFavoriteStatus(clientId: Long, isFavorite: Boolean)
}