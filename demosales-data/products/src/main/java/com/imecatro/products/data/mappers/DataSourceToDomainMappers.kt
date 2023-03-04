package com.imecatro.products.data.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductUnit
import com.imecatro.products.data.model.ProductRoomEntity

fun List<ProductRoomEntity>.toListDomain(): List<ProductDomainModel> {
    return map { it.toDomain() }
}

fun ProductRoomEntity.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price,
        currency = this.currency,
        unit = ProductUnit.values().find { it.symbol == this.unit }
            ?: ProductUnit.Default,
        details = this.details,
        imageUri = this.imageUri
    )
}

fun ProductDomainModel.toData(): ProductRoomEntity {
    return this.id?.let { id ->
        ProductRoomEntity(
            id = id,
            name = this.name ?: "",
            price = this.price ?: 0f,
            currency = this.currency ?: "",
            unit = this.unit.symbol,
            details = this.details,
            imageUri = this.imageUri ?: ""
        )
    } ?: run {
        ProductRoomEntity(
            name = this.name ?: "",
            price = this.price ?: 0f,
            currency = this.currency ?: "",
            unit = this.unit.symbol,
            details = this.details,
            imageUri = this.imageUri ?: ""
        )
    }

}