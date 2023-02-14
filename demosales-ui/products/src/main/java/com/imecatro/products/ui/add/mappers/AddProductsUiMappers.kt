package com.imecatro.products.ui.add.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductUnit
import com.imecatro.products.ui.add.model.AddProductUiModel

fun AddProductUiModel.toDomain(): ProductDomainModel? {
    return ProductDomainModel(
        id = null,
        name = this.name,
        price = this.price?.toFloat() ?: 0f,
        currency = this.currency,
        unit = ProductUnit.values().find { it.symbol == this.unit }
            ?: ProductUnit.Default,//ProductUnit.valueOf(this.unit ?: ProductUnit.Default.symbol),
        details = this.details,
        imageUri = this.imageUri?.toString()

    )
}
