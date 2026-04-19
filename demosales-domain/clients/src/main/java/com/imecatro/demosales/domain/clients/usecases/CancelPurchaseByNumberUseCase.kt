package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class CancelPurchaseByNumberUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<String, Any>(coroutineProvider){
    override suspend fun doInBackground(input: String): Any {
        return clientsRepository.cancelPurchaseByNumber(input)
    }
}