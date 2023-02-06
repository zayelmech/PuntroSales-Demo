package com.imecatro.demosales.domain.products.model

sealed class Currencies(val code: String) {
    object Dollar : Currencies("USD")
    object MexicanPeso : Currencies("MXN")
    object Euros : Currencies("EUR")
}

