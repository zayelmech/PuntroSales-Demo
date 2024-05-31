package com.imecatro.demosales.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.imecatro.demosales.R

sealed class NavigationDirections(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int
) {
    object PRODUCTS :
        NavigationDirections("products_feature", R.string.products, R.drawable.baseline_art_track_24)

    object SALES :
        NavigationDirections("sales_feature", R.string.sales, R.drawable.outline_attach_money_24)

    object CLIENTS :
        NavigationDirections("clients_feature", R.string.clients, R.drawable.round_account_circle_24)
}
