package com.imecatro.domain.products.model


sealed class ProductUnit(val symbol: String) {
    object Kilograms : ProductUnit("kg")
    object Grams : ProductUnit("g")
    object Litters : ProductUnit("L")
    object Milliliters : ProductUnit("ml")
    object Meters : ProductUnit("m")
    object Centimeters : ProductUnit("cm")
    object Default : ProductUnit("pz")
}