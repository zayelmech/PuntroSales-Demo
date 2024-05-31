package com.imecatro.demosales.ui.sales.add.model

import java.math.BigDecimal

data class ProductOnCartUiModel(
    val orderId : Long,
    val product: ProductResultUiModel,
    var qty: Float,
    var subtotal: BigDecimal
)
