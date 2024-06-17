package com.imecatro.demosales.navigation.sales

import kotlinx.serialization.Serializable


sealed class SalesDestinations{

    @Serializable
    object List

    @Serializable
    object Add

    @Serializable
    data class Edit(val id: Int)

    @Serializable
    data class Details(val id: Long)

    @Serializable
    data class Checkout(val id: Int)
}
