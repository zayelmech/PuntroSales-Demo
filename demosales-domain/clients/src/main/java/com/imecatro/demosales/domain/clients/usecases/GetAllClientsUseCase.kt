package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineDispatcher
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Clients Flow typealias
 */
internal typealias ClientsFlow = Flow<List<ClientDomainModel>>

class GetAllClientsUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineDispatcher
) : BackgroundUseCase<Unit, ClientsFlow>(coroutineContext) {

    override suspend fun doInBackground(input: Unit): ClientsFlow =
        clientsRepository.getAllClients()
}