package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineDispatcher
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class UpdateClientUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineDispatcher
) : BackgroundUseCase<ClientDomainModel, Unit>(
    coroutineContext
) {
    override suspend fun doInBackground(input: ClientDomainModel) {
        clientsRepository.updateClient(input)
    }
}