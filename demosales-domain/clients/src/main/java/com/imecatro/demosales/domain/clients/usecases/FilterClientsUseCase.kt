package com.imecatro.demosales.domain.clients.usecases

import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.repository.ClientsRepository
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import kotlinx.coroutines.flow.first

/**
 * Clients Flow typealias
 */
internal typealias Clients = List<ClientDomainModel>

class FilterClientsUseCase(
    private val clientsRepository: ClientsRepository,
    coroutineContext: CoroutineProvider
) : BackgroundUseCase<FilterClientsUseCase.Filters, Clients>(coroutineContext) {

    override suspend fun doInBackground(input: Filters): Clients {

        return if (input.limit != null)
            clientsRepository.getAllClients().first().take(input.limit!!)
        else
            clientsRepository.getAllClients().first()
    }

    suspend operator fun invoke(filterParams: Filters.() -> Unit) : Result<Clients> {
        val filters = Filters().apply(filterParams)
        return execute(filters)
    }

    data class Filters(
        var limit: Int? = null,
    )
}