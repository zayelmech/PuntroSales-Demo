package com.imecatro.demosales.datasource.room.mappers

import com.imecatro.demosales.datasource.room.entities.ProductRoomEntity
import com.imecatro.domain.products.model.ProductDomainModel
import com.imecatro.domain.products.model.ProductUnit

fun List<ProductRoomEntity>.toListDomain(): List<ProductDomainModel> {
    return map { it.toDomain() }
}

fun ProductRoomEntity.toDomain(): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price,
        currency = this.currency,
        unit = ProductUnit::class.nestedClasses.map { it.objectInstance as ProductUnit }
            .find { it.symbol == this.unit } ?: ProductUnit.Default,
        details = this.details,
        imageUri = this.imageUri
    )
}

fun ProductDomainModel.toData(): ProductRoomEntity {
    return ProductRoomEntity(
        id = this.id!!,
        name = this.name!!,
        price = this.price!!,
        currency = this.currency!!,
        unit = this.unit.symbol,
        details = this.details,
        imageUri = this.imageUri.toString()
    )
}