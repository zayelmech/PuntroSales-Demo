package com.imecatro.products.ui.list.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductListItem
import com.imecatro.products.ui.list.model.ProductUiModel
import androidx.core.net.toUri
import java.math.BigDecimal

fun ProductListItem.toProductUiModel() = ProductUiModel(
    id = id, name = name, price = price.toString(), unit = unit,
    stock = BigDecimal.valueOf(stock).toString(), imageUrl = imageUri.toUri(), category = category,
)

fun List<ProductDomainModel>.toProductUiModel(): List<ProductUiModel> {
    return map {
        ProductUiModel(
            id = it.id,
            name = it.name,
            price = it.price?.toString() ?: "0.00",
            unit = it.unit,
            stock = BigDecimal.valueOf(it.stock.quantity).toString(),
            imageUrl = it.imageUri?.toUri(),
            category = it.category?.name
        )
    }
}
