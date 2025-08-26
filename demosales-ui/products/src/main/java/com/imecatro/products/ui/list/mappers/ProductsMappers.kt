package com.imecatro.products.ui.list.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.products.ui.list.model.ProductUiModel
import androidx.core.net.toUri

fun List<ProductDomainModel>.toProductUiModel(): List<ProductUiModel> {
    return map {
        ProductUiModel(
            id = it.id,
            name = it.name,
            price = it.price?.toString() ?: "0.00",
            unit = it.unit,
            stock = "${it.stock.quantity}",
            imageUrl = it.imageUri?.toUri()
        )
    }
}
