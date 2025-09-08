package com.imecatro.demosales.ui.sales.add.model

data class SaleUiModel(
    val id: Long = 0,
    val clientName: String,
    val clientId: Long = 0,
    var date: String,
    var products: List<ProductOnCartUiModel>,
    var totals: SaleChargeUiModel,
    var status: String,
    var note: String,
    val ticketSaved: Boolean = false,
    val clientAddress: String = ""
)

data class SaleChargeUiModel(
    var subtotal: Double,
    var shippingCost: Double,
    var tax: Double,
    var extra: Double,
    var total: Double
)


