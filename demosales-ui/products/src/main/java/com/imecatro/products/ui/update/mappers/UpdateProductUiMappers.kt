package com.imecatro.products.ui.update.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel


fun UpdateProductUiModel.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price?.toDouble() ?: 0.0,
        currency = this.currency,
        unit = this.unit,
        details = this.details,
        stock = ProductStockDomainModel(quantity = this.stock.toDouble(), cost = 0.0, emptyList()),
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