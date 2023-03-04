package com.imecatro.demosales.ui.sales.add.model

data class SaleUiModel(
    var clientName: String,
    var date: String,
    var products: List<ProductOnCartUiModel>,
    var totals: SaleChargeUiModel,
    var status: String,
    var note: String
)

data class SaleChargeUiModel(
    var subtotal: Double,
    var shippingCost: Double,
    var tax: Double,
    var extra: Double,
    var total: Double
)


