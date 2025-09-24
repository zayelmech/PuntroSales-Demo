package com.imecatro.demosales.navigation.products

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

sealed class ProductsDestinations {
    @Serializable
    object List

    @Serializable
    object Add

    @Serializable
    data class Edit(val id: Long)

    @Serializable
    data class Details(val id: Long, val mode : DetailsOf = DetailsOf.Details )

    @Serializable
    data object ListAndDetails

    @Serializable
    data object Categories

    @Keep
    enum class DetailsOf{
        Stock, Details
    }
}

