package com.imecatro.demosales.ui.sales.add.mappers

import androidx.core.net.toUri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel

internal fun ProductDomainModel.toAddSaleUi(qty: Double = 0.0): ProductResultUiModel {
    return ProductResultUiModel(
        id = this.id?:0L,
        name = this.name?:"",
        price = this.price?:0.0,
        imageUri = this.imageUri?.toUri(),
        stock = this.stock.quantity,
        qty = qty
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
        productId = product.id ?: 0L,
        productName = this.product.name ?: "unknown",
        productPrice = this.product.price ?: 0.0,
        qty = this.qty,
        imgUri = this.product.imageUri?.toString()
    )
}

internal fun ProductResultUiModel.toDomain(id: Long = 0): Order {
    return Order(
        id = id,
        productId = this.id ?: 0,
        productName = name ?: "",
        productPrice = price ?: 0.0,
        qty = 1.0,
        imgUri= imageUri?.toString()
    )
}

