package com.imecatro.products.data.details

import com.imecatro.products.data.model.ProductDataModel

interface ProductDetailsDataSource {
    fun getProductFullDetailsById(id: Int) : ProductDataModel
}