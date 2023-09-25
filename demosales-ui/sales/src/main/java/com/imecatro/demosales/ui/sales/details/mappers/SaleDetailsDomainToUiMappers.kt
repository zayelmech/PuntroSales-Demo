package com.imecatro.demosales.ui.sales.details.mappers

import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.details.model.ProductOnTicketUiModel
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel


internal fun SaleDetailsDomainModel.toUi(): TicketDetailsUiModel =
    TicketDetailsUiModel(
        list = list.toUi(),
        client = clientName,
        note = note,
        shippingCost = shippingCost.toString(),
        tax = tax.toString(),
        extra = extra.toString(),
        total = total.toString()
    )

private fun List<Order>.toUi(): List<ProductOnTicketUiModel> {
    return map { order ->
        ProductOnTicketUiModel(
            name = order.productName,
            qty = order.qty,
            subtotal = order.qty * order.productPrice
        )
    }
}
