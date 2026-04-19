package com.imecatro.demosales.domain.clients.repository

import com.imecatro.demosales.domain.clients.exception.ClientNotFoundException
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
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

    override fun getAllFilteredClients(): List<ClientDomainModel> {
        TODO("Not yet implemented")
    }

    override fun deleteClientById(id: Long) {
        //clients.removeAt(id!!)
    }

    override fun updateClient(client: ClientDomainModel) {
        //clients.set(client!!.id, client!!)
    }

    override fun getClientDetailsById(id: Long): ClientDomainModel {
        return clients.find { it.id == id } ?: throw ClientNotFoundException()
    }

    override fun searchClient(letter: String): Flow<List<ClientDomainModel>> {
        return flow { clients.filter { it.name.contains(letter) } }
    }

    override fun getClientByPhoneNumber(phoneNumber: String): ClientDomainModel? {
        TODO("Not yet implemented")
    }

    override fun getPurchasesByClientId(id: Long): Flow<List<PurchaseDomainModel>> {
        TODO("Not yet implemented")
    }

    override fun addPurchase(purchase: PurchaseDomainModel) {
        TODO("Not yet implemented")
    }

    override fun cancelPurchaseByNumber(purchaseNumber: String) {
        TODO("Not yet implemented")
    }
}