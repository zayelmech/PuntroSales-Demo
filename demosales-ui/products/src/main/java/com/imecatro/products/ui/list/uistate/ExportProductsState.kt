package com.imecatro.products.ui.list.uistate

import java.io.File

data class ExportProductsState(
    val ids: List<Long> = emptyList(),
    val isProcessingCatalog: Boolean = false,
    val catalogFile: File? = null,
    val allSelected: Boolean = false
)