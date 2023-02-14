package com.imecatro.demosales.domain.products.model


enum class ProductUnit(val symbol: String) {
    Kilograms("kg"),
    Grams("g"),
    Litters("L"),
    Milliliters("ml"),
    Meters("m"),
    Centimeters("cm"),
    Default("pz")
}