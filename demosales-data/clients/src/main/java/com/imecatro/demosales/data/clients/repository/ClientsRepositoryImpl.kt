package com.imecatro.demosales.data.clients.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.clients.mappers.toData
import com.imecatro.demosales.data.clients.mappers.toDomain
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow


class ClientsRepositoryImpl(
    private val clientsDao: ClientsDao
) : ClientsRepository {

    val version = 3

    @WorkerThread
    override fun addClient(client: ClientDomainModel) {
        clientsDao.addClient(client.toData(4))
    }


    @WorkerThread
    override fun getAllClients(): Flow<List<ClientDomainModel>> {
        return flow {
            clientsDao.getAllClients().collect {
                emit(it.map { client -> client.toDomain() })
            }
        }

    }

    @WorkerThread
    override fun deleteClientById(id: Int) {
        Log.d(TAG, "deleteClientById: $id")
        clientsDao.deleteClientById(id)
    }

    @WorkerThread
    override fun updateClient(client: ClientDomainModel) {
        Log.d(TAG, "updateClient: $client")
        clientsDao.updateClient(client.toData(version))
    }

    @WorkerThread
    override fun getClientDetailsById(id: Int): ClientDomainModel {
        return clientsDao.getClientDetailsById(id).toDomain()
    }

    override fun searchClient(letter: String): Flow<List<ClientDomainModel>> {
        return flow {
            clientsDao.searchClients(letter).collectLatest {
                emit(it.map { client -> client.toDomain() })
            }
        }
    }
}

private const val TAG = "ClientsRepositoryImpl"