package com.imecatro.products.ui.details.mappers

import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel

fun ProductDomainModel.toUiModel(): ProductDetailsUiModel {
    return ProductDetailsUiModel(
        id = this.id,
        name = this.name,
        price = this.price.toString(),
        unit = this.unit.symbol,
        imageUrl = this.imageUri,
        details = this.details
    )
}
