package com.imecatro.products.ui.update.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel


fun UpdateProductUiModel.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price?.toFloat() ?: 0f,
        currency = this.currency,
        unit = this.unit,
        details = this.details,
        stock = 0f,
        imageUri = this.imageUri?.toString()
    )
}

fun ProductDomainModel.toUpdateUiModel(): UpdateProductUiModel {
    return UpdateProductUiModel(
        id = this.id,
        name = this.name,
        price = this.price.toString(),
        currency = this.currency,
        unit = this.unit,
        stock= 0f,
        details = this.details,
        imageUri = Uri.parse(this.imageUri.toString())
    )
}