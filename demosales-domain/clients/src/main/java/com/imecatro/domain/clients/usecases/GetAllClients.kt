package com.imecatro.domain.clients.usecases

import com.imecatro.domain.clients.architecture.BackgroundUseCase
import com.imecatro.domain.clients.model.ClientDomainModel
import com.imecatro.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetAllClients (
    private val clientsRepository: ClientsRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) :BackgroundUseCase<Unit,List<ClientDomainModel>>(coroutineDispatcher){
    override suspend fun executeInBackground(input: Unit): List<ClientDomainModel> {
        return clientsRepository.getAllClients()
    }
}