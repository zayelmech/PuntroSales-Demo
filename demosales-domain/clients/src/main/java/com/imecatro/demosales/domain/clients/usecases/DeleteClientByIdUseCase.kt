package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

/**
 * Hard Delete Client by id
 *
 * When client is deleted, all purchases referencing client id will be deleted to
 */
class DeleteClientByIdUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineProvider
) : BackgroundUseCase<Long, Unit>(coroutineContext) {
    override suspend fun doInBackground(input: Long) {
        clientsRepository.deleteClientById(input)
    }
}