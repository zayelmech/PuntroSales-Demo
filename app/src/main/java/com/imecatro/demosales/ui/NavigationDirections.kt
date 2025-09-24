package com.imecatro.demosales.ui

import androidx.annotation.Keep
import com.imecatro.demosales.R
import kotlinx.serialization.Serializable

@Keep
sealed class NavigationDirections {

    @Serializable
    object ProductsFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.baseline_art_track_24
        override val tittle: Int
            get() = R.string.products
    }

    @Serializable
    object SalesFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.outline_attach_money_24
        override val tittle: Int
            get() = R.string.sales
    }

    @Serializable
    object ClientsFeature : ParentFeature() {
        override val icon: Int
            get() = R.drawable.round_account_circle_24
        override val tittle: Int
            get() = R.string.clients
    }

}

abstract class ParentFeature {
    abstract val icon: Int
    abstract val tittle: Int
}