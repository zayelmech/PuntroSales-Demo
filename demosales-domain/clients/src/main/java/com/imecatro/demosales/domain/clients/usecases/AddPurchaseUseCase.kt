package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository

class AddPurchaseUseCase(
    private val clientsRepository: ClientsRepository
) {
    operator fun invoke(purchase: PurchaseDomainModel) {
        clientsRepository.addPurchase(purchase)
    }
}