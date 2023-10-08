package com.imecatro.domain.clients.usecases

import com.imecatro.domain.clients.architecture.BackgroundUseCase
import com.imecatro.domain.clients.exceptions.ClientsDomainException
import com.imecatro.domain.clients.model.ClientDomainModel
import com.imecatro.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetClientByIdUseCase (
    private val clientsRepository: ClientsRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BackgroundUseCase <Int,ClientDomainModel>(coroutineDispatcher){
    override suspend fun executeInBackground(input: Int): ClientDomainModel {
        if (input <= 0) {
            throw ClientsDomainException.IdError
        } else {
            return clientsRepository.getClientById(input)
        }
    }
}