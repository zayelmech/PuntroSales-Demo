package com.imecatro.demosales.data.clients.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.clients.mappers.toData
import com.imecatro.demosales.data.clients.mappers.toDomain
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ClientsRepositoryImpl(
    private val clientsDao: ClientsDao
) : ClientsRepository {

    val version = 4

    @WorkerThread
    override fun addClient(client: ClientDomainModel) {
        clientsDao.addClient(client.toData(version))
    }


    @WorkerThread
    override fun getAllClients(): Flow<List<ClientDomainModel>> {
        return clientsDao.getAllClients().map {
            it.map { client -> client.toDomain() }
        }
    }

    override fun getAllFilteredClients(): List<ClientDomainModel> {
       return emptyList() // TODO
    }

    @WorkerThread
    override fun deleteClientById(id: Long) {
        Log.d(TAG, "deleteClientById: $id")
        clientsDao.deleteClientById(id)
    }

    @WorkerThread
    override fun updateClient(client: ClientDomainModel) {
        Log.d(TAG, "updateClient: $client")
        clientsDao.updateClient(client.toData(version))
    }

    @WorkerThread
    override fun getClientDetailsById(id: Long): ClientDomainModel {
        return clientsDao.getClientDetailsById(id).toDomain()
    }

    override fun searchClient(letter: String): Flow<List<ClientDomainModel>> {
        return clientsDao.searchClients(letter).map {
            it.map { client -> client.toDomain() }
        }
    }

    override fun getClientByPhoneNumber(phoneNumber: String): ClientDomainModel? {
        return clientsDao.getClientByPhoneNumber(phoneNumber)?.toDomain()
    }

    override fun getPurchasesByClientId(id: Long): Flow<List<PurchaseDomainModel>> {
        return clientsDao.getPurchasesByClientId(id).map {
            it.map { purchase -> purchase.toDomain() }
        }
    }

    override fun addPurchase(purchase: PurchaseDomainModel) {
        clientsDao.addPurchaseAndRecalculate(purchase.toData())
    }

    override fun cancelPurchaseByNumber(purchaseNumber: String) {
        clientsDao.cancelPurchaseAndRecalculate(purchaseNumber)
    }

    override fun updateFavoriteStatus(clientId: Long, isFavorite: Boolean) {
        clientsDao.updateFavoriteStatus(clientId, isFavorite)
    }
}

private const val TAG = "ClientsRepositoryImpl"