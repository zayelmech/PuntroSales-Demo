package com.imecatro.demosales.ui.sales.add.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel

internal fun ProductDomainModel.toAddSaleUi(): ProductResultUiModel {
    return ProductResultUiModel(
        id = this.id?:0,
        name = this.name?:"",
        price = this.price?:0f,
        imageUri = Uri.parse(this.imageUri)
    )
}

internal fun List<ProductDomainModel>.toListAddSaleUi(): List<ProductResultUiModel> {
    return map {
        it.toAddSaleUi()
    }
}


internal fun ProductOnCartUiModel.toOrderDomainModel(id: Long = 0): Order {
    return Order(
        id = id,
        productId = product.id ?: 0,
        productName = this.product.name ?: "unknown",
        productPrice = this.product.price ?: 0f,
        qty = this.qty
    )
}

internal fun ProductResultUiModel.toDomain(id: Long = 0): Order {
    return Order(
        id = id,
        productId = this.id ?: 0,
        productName = name ?: "",
        productPrice = price ?: 0f,
        qty = 1.0
    )
}

