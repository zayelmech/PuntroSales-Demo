package com.imecatro.products.ui.details.model

import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.demosales.ui.theme.architect.Idle
import com.imecatro.demosales.ui.theme.architect.UiState

data class ProductDetailsUiModel(
    val id: Long?,
    val name: String?,
    val price: String?,
    val unit: String?,
    val currency: String?,
    val imageUrl: String?,
    val details: String?,
    val stockQty: String,
    val stockPrice: String,
    val stockHistory: List<History>,
    val productDeleted : Boolean = false,
) : UiState {

    /**
     * @param date is when the movement has been made
     */
    data class History(
        val date: String,
        val qty: String,
        val tittle: String
    )

    override fun isFetchingOrProcessingData(): Boolean {
        return false
    }

    override fun getError(): ErrorUiModel? {
        return null
    }

    companion object : Idle<ProductDetailsUiModel> {
        override val idle: ProductDetailsUiModel
            get() = ProductDetailsUiModel(
                0,
                "Product",
                "0.00",
                "",
                "",
                null,
                "",
                stockQty = "0.0",
                stockPrice = "0",
                emptyList()
            )
    }
}
