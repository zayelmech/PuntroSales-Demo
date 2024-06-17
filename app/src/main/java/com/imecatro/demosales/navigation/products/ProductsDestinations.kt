package com.imecatro.demosales.navigation.products

import kotlinx.serialization.Serializable

sealed class ProductsDestinations {
    @Serializable
    object List

    @Serializable
    object Add

    @Serializable
    data class Edit(val id: Int)

    @Serializable
    data class Details(val id: Int)
}

