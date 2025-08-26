package com.imecatro.demosales.ui.sales.details.mappers

import androidx.core.net.toUri
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.details.model.ProductOnTicketUiModel
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import com.imecatro.demosales.ui.sales.list.mappers.toColor


internal fun SaleDetailsDomainModel.toUi(): TicketDetailsUiModel =
    TicketDetailsUiModel(
        list = list.toUi(),
        client = TicketDetailsUiModel.Client(),
        note = note,
        shippingCost = shippingCost.toString(),
        tax = tax.toString(),
        extra = extra.toString(),
        total = total.toString(),
        statusColor = status.toColor(),
        status = status.str,
        isEditable = (status != OrderStatus.COMPLETED && status != OrderStatus.CANCEL)
    )


internal fun ClientDomainModel.toUi(): TicketDetailsUiModel.Client {
    return TicketDetailsUiModel.Client(id = id, name = name, imageUri = avatarUri?.toUri())
}

private fun List<Order>.toUi(): List<ProductOnTicketUiModel> {
    return map { order ->
        ProductOnTicketUiModel(
            id= order.productId,
            name = order.productName,
            qty = order.qty,
            subtotal = order.qty * order.productPrice
        )
    }
}
