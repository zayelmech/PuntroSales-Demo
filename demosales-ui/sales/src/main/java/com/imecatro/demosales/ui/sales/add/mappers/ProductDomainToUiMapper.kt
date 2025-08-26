package com.imecatro.demosales.ui.sales.add.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import androidx.core.net.toUri

internal fun ProductDomainModel.toAddSaleUi(): ProductResultUiModel {
    return ProductResultUiModel(
        id = this.id?:0L,
        name = this.name?:"",
        price = this.price?:0.0,
        imageUri = this.imageUri?.toUri()
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
        qty = this.qty
    )
}

internal fun ProductResultUiModel.toDomain(id: Long = 0): Order {
    return Order(
        id = id,
        productId = this.id ?: 0,
        productName = name ?: "",
        productPrice = price ?: 0.0,
        qty = 1.0
    )
}

