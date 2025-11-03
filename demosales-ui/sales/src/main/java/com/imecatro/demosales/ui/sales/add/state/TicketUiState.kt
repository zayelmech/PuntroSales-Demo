package com.imecatro.demosales.ui.sales.add.state

import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleUiModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class TicketUiState(
    val ticket: SaleUiModel,
    val isSearchingProducts: Boolean = false,
    val isSearchingClients: Boolean = false,

    val subtotal : String = "",
    val productNotFount : Boolean?=null,
    ) : UiState {

    override fun isFetchingOrProcessingData(): Boolean {
        return isSearchingProducts || isSearchingClients
    }

    override fun getError(): ErrorUiModel? {
        return null
    }

    companion object : Idle<TicketUiState> {

        private val initCharges = SaleChargeUiModel(0.0, 0.0, 0.0, 0.0, 0.0)

        private val initialTicket = SaleUiModel(
            clientName = "",
            date = "",
            products = listOf(),
            totals = initCharges,
            status = "",
            note = "",
        )

        override val idle: TicketUiState
            get() = TicketUiState(ticket = initialTicket)


    }
}
