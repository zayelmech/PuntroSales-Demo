package com.imecatro.demosales.domain.clients.repository

import com.imecatro.demosales.domain.clients.exception.ClientNotFoundException
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


internal class DummyClientsRepositoryImpl : ClientsRepository {

    private val clients =
        mutableListOf<ClientDomainModel>(ClientDomainModel(1, "Client", "91111", "street", ""))

    override fun addClient(client: ClientDomainModel) {
        clients.add(client!!)
    }

    override fun getAllClients(): Flow<List<ClientDomainModel>> {
        return flowOf(clients)
    }

    override fun deleteClientById(id: Int) {
        clients.removeAt(id!!)
    }

    override fun updateClient(client: ClientDomainModel) {
        clients.set(client!!.id, client!!)
    }

    override fun getClientDetailsById(id: Int): ClientDomainModel {
        return clients.find { it.id == id } ?: throw ClientNotFoundException()
    }

    override fun searchClient(letter: String): Flow<List<ClientDomainModel>> {
        return flow { clients.filter { it.name.contains(letter) } }
    }
}