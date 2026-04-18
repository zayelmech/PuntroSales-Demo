package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class GetClientByPhoneNumberUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineProvider
) : BackgroundUseCase<String, ClientDomainModel?>(coroutineContext) {
    override suspend fun doInBackground(input: String): ClientDomainModel? =
        clientsRepository.getClientByPhoneNumber(input)
}