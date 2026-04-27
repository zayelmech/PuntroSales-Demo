package com.imecatro.demosales.ui

import androidx.annotation.Keep
import com.imecatro.demosales.R
import kotlinx.serialization.Serializable

/**
 * Sealed class defining the top-level navigation directions for the application.
 *
 * Each object representing a feature module is marked as [Serializable] to be used
 * with Type-Safe Navigation.
 */
@Keep
sealed class NavigationDirections {

    /**
     * Navigation destination for the Products feature.
     */
    @Serializable
    @Keep
    object ProductsFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.baseline_art_track_24
        override val tittle: Int
            get() = R.string.products
    }

    /**
     * Navigation destination for the Sales feature.
     */
    @Serializable
    @Keep
    object SalesFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.outline_attach_money_24
        override val tittle: Int
            get() = R.string.sales
    }

    /**
     * Navigation destination for the Clients feature.
     */
    @Serializable
    @Keep
    object ClientsFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.round_account_circle_24
        override val tittle: Int
            get() = R.string.clients
    }

}

/**
 * Abstract base class for top-level features in the application's navigation.
 *
 * @property icon The resource ID of the icon associated with the feature.
 * @property tittle The resource ID of the title string associated with the feature.
 */
abstract class ParentFeature {
    abstract val icon: Int
    abstract val tittle: Int
}