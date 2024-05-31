package com.imecatro.demosales.ui.sales.add.mappers

import androidx.core.net.toUri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel


internal fun SaleDomainModel.toUi(): List<ProductOnCartUiModel> {
    return this.productsList.map { orderDomain ->
        ProductOnCartUiModel(
            orderId = orderDomain.id,
            product = orderDomain.toUi(),
            qty = orderDomain.qty,
            subtotal = (orderDomain.qty * (orderDomain.productPrice)).toBigDecimal()
        )
    }
} 


internal fun Order.toUi(): ProductResultUiModel =
    ProductResultUiModel(
        id = this.productId,
        name = this.productName,
        price = this.productPrice,
        imageUri = this.imgUri?.toUri()
    )

fun List<ProductOnCartUiModel>.toDomainModel(): SaleDomainModel {

    return SaleDomainModel(
        id = 0,
        clientId = 0,
        date = "",
        productsList = map { it.toOrderDomainModel() },
        total = 0.0,
        status = OrderStatus.PENDING
    )
}

internal fun ProductDomainModel.toCartUiModel(): ProductOnCartUiModel {
    return ProductOnCartUiModel(
        orderId = 0,
        product = this.toAddSaleUi(),
        qty = 0f,
        subtotal = 0f.toBigDecimal()
    )
}
