package com.imecatro.products.data.add.datasource

import com.imecatro.products.data.model.ProductDataModel

interface ListDataSource {
    fun getALlProducts() : List<ProductDataModel>
}