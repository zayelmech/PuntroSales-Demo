package com.imecatro.products.ui.update.mappers

import android.net.Uri
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit
import com.imecatro.products.ui.update.model.UpdateProductUiModel


 fun UpdateProductUiModel.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price?.toFloat() ?: 0f,
        currency = this.currency,
        unit = ProductUnit.Default,
        details = this.details,
        imageUri = this.imageUri?.toString()
    )
}

fun ProductDomainModel.toUi() : UpdateProductUiModel{
    return UpdateProductUiModel(
        id = this.id,
        name = this.name,
        price = this.price.toString(),
        currency = this.currency,
        unit = this.unit.symbol,
        details = this.details,
        imageUri = Uri.parse( this.imageUri.toString())
    )
}