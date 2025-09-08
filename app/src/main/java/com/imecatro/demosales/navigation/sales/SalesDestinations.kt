package com.imecatro.demosales.navigation.sales

import kotlinx.serialization.Serializable


sealed class SalesDestinations {

    @Serializable
    object List

    @Serializable
    data class Add(val id: Long? = null)

    @Serializable
    data class Details(val id: Long)

    @Serializable
    data class SuccessDetails(val id: Long)

    @Serializable
    data class Checkout(val id: Long)
}
