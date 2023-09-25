package com.imecatro.demosales.ui.sales.list.model

data class SaleOnListUiModel(
    val id : Long,
    var clientName: String,
    var date: String,
    var total: Double,
    var status: String,
)

internal typealias SalesList = List<SaleOnListUiModel>