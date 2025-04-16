package com.imecatro.products.ui.details.model

data class ProductDetailsUiModel(
    val id: Int?,
    val name: String?,
    val price: String?,
    val unit: String?,
    val currency: String?,
    val imageUrl: String?,
    val details: String?,
    val stockQty  : String = "",
    val stockHistory : List<History> = emptyList()
) {

    /**
     * @param date is when the movement has been made
     */
    data class History(
        val date: String,
        val qty: String,
        val tittle: String
    )
}
