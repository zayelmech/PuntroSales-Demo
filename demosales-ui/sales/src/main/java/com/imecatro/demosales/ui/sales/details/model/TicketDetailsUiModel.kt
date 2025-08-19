package com.imecatro.demosales.ui.sales.details.model

import android.net.Uri

data class TicketDetailsUiModel(
    val list: List<ProductOnTicketUiModel>,
    val client: Client = Client(),
    val note: String = "",
    val shippingCost: String = "",
    val tax: String = "",
    val extra: String = "",
    val total: String = "",
    val isEditable: Boolean = true,
) {
    data class Client(
        val id: Long = 0,
        val name: String = "",
        val imageUri: Uri? = null
    )
}

data class ProductOnTicketUiModel(
    val name: String,
    val qty: Double,
    val subtotal: Double
)