package com.imecatro.ui.products.mappers

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit
import com.imecatro.ui.products.model.ProductUiModel

fun List<ProductDomainModel>.toProductUiModel(): List<ProductUiModel> {
    return map {
        ProductUiModel(
            id = it.id,
            name = it.name,
            price = it.price?.toString() ?: "0.00",
            unit = it.unit.name,
            imageUrl = it.imageUri
        )
    }
}
