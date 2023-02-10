package com.imecatro.demosales.ui.sales.add.uistate

import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel

sealed class TicketUiState(val status: Int) {
    object Initialized : TicketUiState(0)
    data class OnCache(val cart: List<ProductOnCartUiModel>) : TicketUiState(1)
    object Saved : TicketUiState(2)
    data class Error(val message: String) : TicketUiState(4)
}
