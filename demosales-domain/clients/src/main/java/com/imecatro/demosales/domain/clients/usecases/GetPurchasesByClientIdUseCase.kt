package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow

class GetPurchasesByClientIdUseCase(
    private val clientsRepository: ClientsRepository
) {
    operator fun invoke(id: Long): Flow<List<PurchaseDomainModel>> {
        return clientsRepository.getPurchasesByClientId(id)
    }
}