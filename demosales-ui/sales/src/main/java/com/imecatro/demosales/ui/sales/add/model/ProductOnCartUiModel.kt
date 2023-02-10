package com.imecatro.demosales.ui.sales.add.model

data class ProductOnCartUiModel(
    val product: ProductResultUiModel,
    var qty: Float,
    var subtotal: Float
)
