package com.imecatro.demosales.ui.sales.add.viewmodel

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.usecases.FilterClientsUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.SearchClientUseCase
import com.imecatro.demosales.domain.products.usecases.RemoveFromStockUseCase
import com.imecatro.demosales.domain.sales.add.usecases.CheckoutSaleUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateSaleClientUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.mappers.toUi
import com.imecatro.demosales.ui.sales.add.model.ClientResultUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.state.TicketUiState
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CheckoutViewModel.Factory::class)
class CheckoutViewModel @AssistedInject constructor(
    @Assisted("saleId") private val saleId: Long,
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val saveSaleUseCase: CheckoutSaleUseCase,
    private val searchClientUseCase: SearchClientUseCase,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val filterClientsUseCase: FilterClientsUseCase,
    private val removeFromStockUseCase: RemoveFromStockUseCase,
    private val updateSaleClientUseCase: UpdateSaleClientUseCase
) : BaseViewModel<TicketUiState>(TicketUiState.idle) {

    private val _results: MutableStateFlow<List<ClientResultUiModel>> =
        MutableStateFlow(emptyList())
    val clientsFound: StateFlow<List<ClientResultUiModel>> = _results.onStart {
        fetchMostPopularClients()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private fun fetchMostPopularClients() {
        viewModelScope.launch {
            val result = filterClientsUseCase { limit = 10 }
            result.onSuccess { list ->
                _results.update { list.toUi() }
            }
        }
    }

    fun onNoteChangeAction(note: String) {
        updateState {
            val updatedTicket = ticket.copy(note = note)
            copy(ticket = updatedTicket)
        }
    }

    private fun onGetDetailsAction(saleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ticketD = getDetailsOfSaleByIdUseCase(saleId)
            updateState {
                val updatedTicket = ticket.copy(
                    id = saleId,
                    note = ticketD.note,
                    products = ticketD.list.toUi(),
                    status = ticketD.status.str,
                    totals = ticket.totals.copy(subtotal = ticketD.total, extra = ticketD.extra)
                )
                copy(ticket = updatedTicket)
            }
            getClientDetailsByIdUseCase.execute(ticketD.clientId).onSuccess {
                updateState {
                    val updatedTicket = ticket.copy(clientName = it.name, clientId = it.id)
                    copy(ticket = updatedTicket)
                }
            }
        }
    }

    /**
     * @param extra is the amount extra that must be added to the total
     */
    fun onExtraChargeAdded(extra: String) {
        // Attempt to parse the string to a Double.
        // toDoubleOrNull() is safer as it returns null for invalid input.
        val extraAmount = extra.toDoubleOrNull()

        // Validate the parsed amount.
        // 1. Check if parsing failed (extraAmount is null).
        // 2. Check if the amount is negative.
        if (extraAmount == null || extraAmount < 0) {
            // Handle invalid input appropriately.
            // For example, log an error, show a message to the user, etc.
            println("Invalid extra charge amount: $extra") // Replace with actual error handling
            return
        }
        updateState {
            val updatedTicket = ticket.copy(
                totals = ticket.totals.copy(
                    extra = extraAmount,
                    total = ticket.totals.subtotal + extraAmount
                )
            )
            copy(ticket = updatedTicket)
        }
    }


    fun onCheckoutAction() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTicket = uiState.value.ticket
            saveSaleUseCase(currentTicket.id) {
                note = currentTicket.note
                date = System.currentTimeMillis().toString()
                totals = currentTicket.totals.toDomain()
                client = SaleDomainModel.Client(
                    id = currentTicket.clientId,
                    name = currentTicket.clientName,
                    address = currentTicket.clientAddress
                )
                tickedPaid = true
            }
            deductStock(currentTicket.id)
            updateState {
                copy(ticket = ticket.copy(ticketSaved = true))
            }
        }
    }

    fun onSearchClientAction(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchClientUseCase(query).collect { results ->
                _results.update { results.toUi() }

            }
        }
    }

    //todo save client data on db
    fun onAddClientAction(client: ClientResultUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "onAddClientAction: $client")
            updateSaleClientUseCase(saleId) {
                id = client.id
                name = client.name
                address = client.address
            }
            updateState {
                val updatedTicket = ticket.copy(
                    clientName = client.name,
                    clientId = client.id,
                    clientAddress = client.address
                )
                copy(ticket = updatedTicket)
            }
        }

    }

    fun onSavePendingTicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTicket = uiState.value.ticket
            saveSaleUseCase(currentTicket.id) {
                note = currentTicket.note
                date = System.currentTimeMillis().toString()
                totals = currentTicket.totals.toDomain()
                client = SaleDomainModel.Client(
                    id = currentTicket.clientId,
                    name = currentTicket.clientName,
                    address = currentTicket.clientAddress
                )
                tickedPaid = false
            }
            deductStock(currentTicket.id)

            updateState {
                copy(ticket = ticket.copy(ticketSaved = true))
            }
        }
    }

    private suspend fun deductStock(saleId: Long) {
        val currentTicket = uiState.value.ticket

        //todo improve this mechanism on db
        // Validates products already deducted when sale was pending
        if (currentTicket.status == OrderStatus.PENDING.str) return

        currentTicket.products.forEach { product ->
            removeFromStockUseCase(
                reference = "Sale #$saleId",
                productId = product.product.id,
                amount = product.qty
            )
        }
    }

    override fun onStart() = onGetDetailsAction(saleId)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("saleId") saleId: Long
        ): CheckoutViewModel
    }

}

private fun List<ClientDomainModel>.toUi(): List<ClientResultUiModel> {
    return map { client ->
        ClientResultUiModel(
            id = client.id,
            name = client.name,
            address = client.shipping,
            imageUri = client.avatarUri?.toUri()
        )
    }
}


private fun SaleChargeUiModel.toDomain(): SaleDomainModel.Costs {
    return SaleDomainModel.Costs(
        extraCost = this.extra,
        subTotal = this.subtotal,
        total = this.total
    )
}
