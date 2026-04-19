package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class UpdateFavoriteStatusUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<UpdateFavoriteStatusUseCase.Input, Unit>(coroutineProvider) {

    data class Input(val clientId: Long, val isFavorite: Boolean)

    override suspend fun doInBackground(input: Input) {
        clientsRepository.updateFavoriteStatus(input.clientId, input.isFavorite)
    }
}
