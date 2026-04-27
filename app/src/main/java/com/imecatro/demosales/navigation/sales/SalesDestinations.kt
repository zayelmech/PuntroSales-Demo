package com.imecatro.demosales.navigation.sales

import kotlinx.serialization.Serializable


/**
 * Defines the navigation destinations for the Sales feature.
 */
sealed class SalesDestinations {

    /** Destination for the list of sales. */
    @Serializable
    object List

    /**
     * Destination for adding a new sale.
     * @property id Optional ID if duplicating an existing sale.
     */
    @Serializable
    data class Add(val id: Long? = null)

    /**
     * Destination for viewing sale details.
     * @property id The ID of the sale to view.
     */
    @Serializable
    data class Details(val id: Long)

    /**
     * Destination shown after a successful sale creation.
     * @property id The ID of the created sale.
     */
    @Serializable
    data class SuccessDetails(val id: Long)

    /**
     * Destination for the checkout process.
     * @property id The ID of the sale being checked out.
     */
    @Serializable
    data class Checkout(val id: Long)
}
