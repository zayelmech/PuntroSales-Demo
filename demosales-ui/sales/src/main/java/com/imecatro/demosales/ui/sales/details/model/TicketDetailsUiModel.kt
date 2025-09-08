package com.imecatro.demosales.ui.sales.details.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TicketDetailsUiModel(
    val id: Long = 0,
    val list: List<ProductOnTicketUiModel>,
    val client: Client = Client(),
    val note: String = "",
    val discount: String = "",
    val extra: String = "",
    val total: String = "",
    val status: String = "",
    val statusColor: Color = Color.Unspecified,
    val isReadyToPay: Boolean = false,
    val isCancelable: Boolean = false,
    val isDraft : Boolean = true,
    val isAlreadyPaid : Boolean = false
) {
    data class Client(
        val id: Long = 0,
        val name: String = "",
        val imageUri: Uri? = null
    )
}

data class ProductOnTicketUiModel(
    val id: Long,
    val name: String,
    val qty: Double,
    val subtotal: Double
)