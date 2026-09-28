package com.imecatro.demosales.ui.sales.fulfillment.model

import java.math.BigDecimal

data class FulfillmentPlanUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val orderCount: Int = 0,
    val clientCount: Int = 0,
    val products: List<FulfillmentProductUiModel> = emptyList()
) {
    val shoppingList: List<FulfillmentProductUiModel>
        get() = products.filter { it.missing?.signum() == 1 }

    val needsReview: Boolean
        get() = products.any { it.available == null || it.unit == null }

    val canShare: Boolean
        get() = !isLoading && !hasError && !needsReview && shoppingList.isNotEmpty()
}

data class FulfillmentProductUiModel(
    val id: Long,
    val name: String,
    val unit: String?,
    val demand: BigDecimal,
    val available: BigDecimal?,
    val missing: BigDecimal?,
    val orders: List<FulfillmentOrderUiModel>
)

data class FulfillmentOrderUiModel(
    val id: Long,
    val clientName: String,
    val quantity: BigDecimal
)
