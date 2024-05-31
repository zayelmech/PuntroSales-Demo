package com.imecatro.demosales.navigation.sales


sealed class SalesDestinations(val route: String) {

    object List : SalesDestinations("GetListSalesRoute")

    object Add : SalesDestinations("AddSalesRoute")

    object Edit : SalesDestinations("EditSalesRoute")

    object Details : SalesDestinations("DetailsSalesRoute")

    object Checkout : SalesDestinations("CheckoutSalesRoute")
}
