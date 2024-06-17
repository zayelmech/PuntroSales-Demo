package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.AppCoroutineDispatcher
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class DeleteClientByIdUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: AppCoroutineDispatcher
) : BackgroundUseCase<Int, Unit>(coroutineContext) {
    override suspend fun doInBackground(input: Int) {
        clientsRepository.deleteClientById(input)
    }
}