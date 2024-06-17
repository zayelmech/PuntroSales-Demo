package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.AppCoroutineDispatcher
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class GetClientDetailsByIdUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: AppCoroutineDispatcher
) : BackgroundUseCase<Int, ClientDomainModel>(coroutineContext) {
    override suspend fun doInBackground(input: Int): ClientDomainModel {
        return clientsRepository.getClientDetailsById(input)
    }
}