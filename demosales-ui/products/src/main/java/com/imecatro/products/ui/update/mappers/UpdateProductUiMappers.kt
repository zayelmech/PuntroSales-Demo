package com.imecatro.products.ui.update.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import androidx.core.net.toUri
import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel


fun UpdateProductUiModel.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price?.toDouble() ?: 0.0,
        currency = this.currency,
        unit = this.unit,
        details = this.details,
        stock = ProductStockDomainModel(quantity = this.stock.toDouble(), cost = 0.0, emptyList()),
        imageUri = this.imageUri?.toString(),
        category = this.category?.let { ProductCategoryDomainModel(name = it) }
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
        imageUri = this.imageUri.toString().toUri(),
        category = this.category?.name
    )
}