package com.imecatro.demosales.ui.sales.add.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel

internal fun ProductDomainModel.toAddSaleUi(): ProductResultUiModel {
    return ProductResultUiModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUri = Uri.parse(this.imageUri)
    )
}

internal fun List<ProductDomainModel>.toListAddSaleUi(): List<ProductResultUiModel> {
    return map {
        it.toAddSaleUi()
    }
}

internal fun ProductDomainModel.toCartUiModel(): ProductOnCartUiModel {
    return ProductOnCartUiModel(
        product = this.toAddSaleUi(),
        qty = 0f,
        subtotal = 0f.toBigDecimal()
    )
}

internal fun ProductOnCartUiModel.toOrderDomainModel(): Order {
    return Order(
        productId = product.id?: 0,
        productName = this.product.name ?: "unknown",
        productPrice = this.product.price ?: 0f,
        qty = this.qty
    )
}