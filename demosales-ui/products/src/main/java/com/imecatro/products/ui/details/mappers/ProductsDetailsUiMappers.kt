package com.imecatro.products.ui.details.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.update.mappers.toUpdateUiModel

fun ProductDomainModel.toUiModel(): ProductDetailsUiModel {
    return ProductDetailsUiModel(
        id = this.id,
        name = this.name,
        price = this.price.toString(),
        currency = this.currency,
        unit = this.unit,
        imageUrl = this.imageUri,
        details = this.details,
        stockQty = "${this.stock.quantity}",
        stockPrice = "$${this.stock.cost} ${this.currency}",
        stockHistory = this.stock.history.toUi()
    )
}

private fun List<ProductStockDomainModel.History>.toUi(): List<ProductDetailsUiModel.History> {

    return map { stock ->
        ProductDetailsUiModel.History(
            date = stock.date,
            qty = "${stock.qty}",
            tittle = stock.tittle
        )
    }
}
