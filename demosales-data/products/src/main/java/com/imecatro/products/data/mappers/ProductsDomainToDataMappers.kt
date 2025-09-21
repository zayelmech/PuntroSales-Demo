package com.imecatro.products.data.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.products.data.model.ProductRoomEntity


fun ProductDomainModel.toData(): ProductRoomEntity {
    return this.id?.let { id ->
        ProductRoomEntity(
            id = id,
            name = this.name ?: "",
            price = this.price ?: 0.0,
            currency = this.currency ?: "",
            unit = this.unit ?: "",
            details = this.details,
            imageUri = this.imageUri ?: "",
            stock = this.stock.quantity,
            categoryId = this.category?.id
        )
    } ?: run {
        ProductRoomEntity(
            name = this.name ?: "",
            price = this.price ?: 0.0,
            currency = this.currency ?: "",
            unit = this.unit ?: "",
            details = this.details,
            imageUri = this.imageUri ?: "",
            stock = this.stock.quantity,
            categoryId = this.category?.id
        )
    }

}