package com.imecatro.demosales.navigation.products

import kotlinx.serialization.Serializable

sealed class ProductsDestinations {
    @Serializable
    object List

    @Serializable
    object Add

    @Serializable
    data class Edit(val id: Long)

    @Serializable
    data class Details(val id: Long)
}

