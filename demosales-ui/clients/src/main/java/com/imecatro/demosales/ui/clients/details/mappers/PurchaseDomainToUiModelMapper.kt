package com.imecatro.demosales.ui.clients.details.mappers

import com.imecatro.demosales.domain.clients.model.PurchaseDomainModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal fun toPurchaseUiModel(purchase: PurchaseDomainModel): PurchaseUiModel {
    return PurchaseUiModel(
        id = purchase.id,
        purchaseNumber = purchase.purchaseNumber,
        description = purchase.description,
        amount = String.format(Locale.getDefault(), "$%.2f", purchase.amount),
        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
            Date(purchase.date)
        )
    )
}