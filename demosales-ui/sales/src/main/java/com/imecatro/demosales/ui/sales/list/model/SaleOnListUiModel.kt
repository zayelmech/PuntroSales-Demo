package com.imecatro.demosales.ui.sales.list.model

import androidx.compose.ui.graphics.Color

data class SaleOnListUiModel(
    val id : Long,
    val clientName: String,
    val date: String,
    val total: Double,
    val status: String,
    val statusColor : Color,
    val isSelected : Boolean = false
)

internal typealias SalesList = List<SaleOnListUiModel>
