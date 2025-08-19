package com.imecatro.demosales.ui.sales.list.model

import androidx.compose.ui.graphics.Color

data class SaleOnListUiModel(
    val id : Long,
    var clientName: String,
    var date: String,
    var total: Double,
    var status: String,
    val statusColor : Color
)

internal typealias SalesList = List<SaleOnListUiModel>