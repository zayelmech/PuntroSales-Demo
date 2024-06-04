package com.imecatro.demosales.domain.clients.repository

import com.imecatro.demosales.domain.clients.exception.ClientNotFoundException
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import kotlinx.coroutines.flow.Flow

/**
 *
 * @author imecatro
 *
 * @version 1.0
 * @method addClient add a new client
 * @method getAllClients get all clients
 * @method deleteClientById delete a client by id
 * @method updateClient update a client
 * @method getClientDetailsById get a client details by id
 * @method searchClient search a client
 *
 */
interface ClientsRepository {
    fun addClient(client: ClientDomainModel?)
    fun getAllClients(): Flow<List<ClientDomainModel>>
    fun deleteClientById(id: Int?)
    fun updateClient(client: ClientDomainModel?)
    @Throws(ClientNotFoundException::class)
    fun getClientDetailsById(id: Int?): ClientDomainModel
    fun searchClient(letter: String): Flow<List<ClientDomainModel>>

}