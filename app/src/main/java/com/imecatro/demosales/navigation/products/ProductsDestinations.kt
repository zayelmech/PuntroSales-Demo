package com.imecatro.demosales.navigation.products

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the Products feature.
 *
 * This class uses Type-Safe Navigation with `Serializable` objects.
 */
sealed class ProductsDestinations {
    /** Destination for the product list screen. */
    @Serializable
    object List

    /** Destination for the add product screen. */
    @Serializable
    object Add

    /**
     * Destination for the edit product screen.
     * @property id The ID of the product to edit.
     */
    @Serializable
    data class Edit(val id: Long)

    /**
     * Destination for the product details screen.
     * @property id The ID of the product.
     * @property mode The details mode (e.g., Stock, Details).
     */
    @Serializable
    data class Details(val id: Long, val mode : DetailsOf = DetailsOf.Details )

    /** Destination for the adaptive list-detail layout. */
    @Serializable
    data object ListAndDetails : ProductsDestinations()

    /** Destination for the categories screen. */
    @Serializable
    data object Categories

    /** Enum representing the details view mode. */
    @Keep
    enum class DetailsOf{
        Stock, Details
    }
}

