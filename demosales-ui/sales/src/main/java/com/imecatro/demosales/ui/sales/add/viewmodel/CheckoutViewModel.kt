package com.imecatro.demosales.ui.sales.add.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.usecases.SearchClientUseCase
import com.imecatro.demosales.domain.sales.add.usecases.CheckoutSaleUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.model.ClientResultUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val saveSaleUseCase: CheckoutSaleUseCase,
    private val searchClientUseCase: SearchClientUseCase

) : ViewModel() {


    private val initCharges = SaleChargeUiModel(0.0, 0.0, 0.0, 0.0, 0.0)
    private val initialTicket = SaleUiModel(0, "Guest", 0, "", listOf(), initCharges, "default", "")
    val currentTicket: MutableStateFlow<SaleUiModel> = MutableStateFlow(initialTicket)

    private val _results: MutableStateFlow<List<ClientResultUiModel>> =
        MutableStateFlow(emptyList())
    val clientsFound: StateFlow<List<ClientResultUiModel>> = _results.onStart {
        fetchMostPopularClients()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private fun fetchMostPopularClients() {
        viewModelScope.launch {
//            val filtered = TODO()
//            _results.update { filtered }
        }
    }

    fun onNoteChangeAction(note: String) {
        currentTicket.update { it.copy(note = note) }
    }

    fun onGetDetailsAction(saleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ticket = getDetailsOfSaleByIdUseCase(saleId)
            currentTicket.update {
                it.copy(
                    id = saleId,
                    totals = initCharges.copy(subtotal = ticket.total)
                )
            }
        }
    }

    /**
     * @param extra is the amount extra that must be added to the total
     */
    fun onExtraChargeAdded(extra: String) {
        //todo validates extra amount
        currentTicket.update { it.copy(totals = it.totals.copy(extra = extra.toDouble())) }
        currentTicket.update { it.copy(totals = it.totals.copy(total = extra.toDouble() + it.totals.subtotal)) }
    }


    fun onCheckoutAction() {
        viewModelScope.launch(Dispatchers.IO) {
            saveSaleUseCase(currentTicket.value.id) {
                note = currentTicket.value.note
                date = System.currentTimeMillis().toString()
                totals = currentTicket.value.totals.toDomain()
                clientId = currentTicket.value.clientId
                tickedPaid = true
            }
            currentTicket.update { it.copy(ticketSaved = true) }
        }
    }

    fun onSearchClientAction(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchClientUseCase(query).collect { results ->
                _results.update { results.toUi() }

            }
        }
    }

    fun onAddClientAction(client: ClientResultUiModel) {
        currentTicket.update { it.copy(clientName = client.name, clientId = client.id) }
    }

    fun onSavePendingTicked() {
        viewModelScope.launch(Dispatchers.IO) {
            saveSaleUseCase(currentTicket.value.id) {
                note = currentTicket.value.note
                date = System.currentTimeMillis().toString()
                totals = currentTicket.value.totals.toDomain()
                clientId = currentTicket.value.clientId
                tickedPaid = false
            }
            currentTicket.update { it.copy(ticketSaved = true) }
        }
    }

}

private fun List<ClientDomainModel>.toUi(): List<ClientResultUiModel> {
    return map { client ->
        ClientResultUiModel(
            id = client.id,
            name = client.name,
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
