package com.imecatro.demosales.ui.sales.list.state

import java.io.File


data class ExportSalesReportInput(
    val ids: List<Long> = emptyList(),
    val isFetchingData: Boolean = false,
    val salesFile: File? = null,
    val groupedProductsFile: File? = null,
    val allSelected: Boolean = false
)