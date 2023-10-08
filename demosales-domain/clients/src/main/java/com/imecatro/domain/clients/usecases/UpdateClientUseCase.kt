package com.imecatro.domain.clients.usecases

import com.imecatro.domain.clients.architecture.BackgroundUseCase
import com.imecatro.domain.clients.exceptions.ClientsDomainException
import com.imecatro.domain.clients.model.ClientDomainModel
import com.imecatro.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UpdateClientUseCase (
    private val clientsRepository: ClientsRepository,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) :BackgroundUseCase<ClientDomainModel,Unit>(coroutineDispatcher){
    override suspend fun executeInBackground(input: ClientDomainModel) {
        if (input.name.isEmpty()) {
            throw ClientsDomainException.ClientFieldIsEmpty
        } else if (input.email.isEmpty()) {
            throw ClientsDomainException.EmailClientFieldIsEmpty
        } else if (input.phone.isEmpty()) {
            throw ClientsDomainException.PhoneClientFieldIsEmpty
        } else {
            clientsRepository.updateClient(input)
        }
    }
}