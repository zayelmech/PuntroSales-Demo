package com.imecatro.demosales.navigation.products

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable

@Serializable
sealed class CatalogDestinations {
    /**
     * Destination for the catalog maker screen.
     * @property ids Collection of product IDs to include in the catalog.
     */
    @Serializable
    data class CatalogMaker(val ids: Collection<Long>)

    @Serializable
    data class PreviewCatalog(val url: String)
    @Serializable
    data class Publicated(val url : String) {

    }

    @Serializable
    data object Share {

    }

    @Serializable
    data object Authentication

    @Serializable
    data object Publishing

    @Serializable
    data object Summary

    @Serializable
    data object Management

    @Serializable
    data class QRView(val url: String)
}