package com.imecatro.domain.clients.repository

import com.imecatro.domain.clients.model.ClientDomainModel
import kotlinx.coroutines.flow.Flow

interface ClientsRepository {

    fun addClient(client: ClientDomainModel)
    fun updateClient(client: ClientDomainModel)
    fun deleteClientById(id:Int)
    fun getClientById(id: Int) : ClientDomainModel
    fun getAllClients(): List<ClientDomainModel>

}