package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class AddClientUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineProvider
) :
    BackgroundUseCase<ClientDomainModel, Unit>(coroutineContext) {
    override suspend fun doInBackground(input: ClientDomainModel) =
        clientsRepository.addClient(input)
}