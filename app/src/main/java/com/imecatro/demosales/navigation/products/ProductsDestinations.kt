package com.imecatro.demosales.navigation.products

sealed class ProductsDestinations(val route: String) {
    object List : ProductsDestinations("GetListProductsRoute")
    object Add : ProductsDestinations("AddProductsRoute")
    object Edit : ProductsDestinations("EditProductsRoute")
    object Details : ProductsDestinations("DetailsProductsRoute")
}

