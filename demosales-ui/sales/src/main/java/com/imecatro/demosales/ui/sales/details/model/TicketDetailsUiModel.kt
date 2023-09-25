package com.imecatro.demosales.ui.sales.details.model

data class TicketDetailsUiModel(
    val list: List<ProductOnTicketUiModel>,
    val client: String = "Guest",
    val note: String = "",
    val shippingCost: String = "",
    val tax: String = "",
    val extra: String = "",
    val total: String = ""
)

data class ProductOnTicketUiModel(
    val name: String,
    val qty: Float,
    val subtotal: Float
)