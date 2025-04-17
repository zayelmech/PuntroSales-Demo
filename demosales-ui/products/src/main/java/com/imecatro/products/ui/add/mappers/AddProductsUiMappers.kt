package com.imecatro.products.ui.add.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.ui.add.model.AddProductUiModel

//Design patter : Adapter
internal fun AddProductUiModel.toDomain(): ProductDomainModel? {

    return ProductDomainModel(
        id = null,
        name = this.name,
        price = this.price?.toFloat() ?: 0f,
        currency = this.currency,
        unit = this.unit,
        details = this.details,
        imageUri = this.imageUri?.toString(),
        stock = ProductStockDomainModel(quantity = this.stock.filter { it.isDigit() || it == '.' }.toDouble(), cost = 0.0, history = emptyList())

    )
}
