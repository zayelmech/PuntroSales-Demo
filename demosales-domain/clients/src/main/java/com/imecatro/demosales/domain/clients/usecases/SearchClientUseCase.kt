package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow

class SearchClientUseCase(
    private val clientsRepository: ClientsRepository
) {


    suspend operator fun invoke(query : String): Flow<List<ClientDomainModel>> {
        return clientsRepository.searchClient(query)
    }
}