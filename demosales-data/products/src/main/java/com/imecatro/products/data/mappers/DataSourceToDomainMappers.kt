package com.imecatro.products.data.mappers

import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity

fun List<ProductRoomEntity>.toListDomain(): List<ProductDomainModel> {
    return map { it.toDomain() }
}

fun ProductRoomEntity.toDomain(stock: List<StockRoomEntity> = emptyList()): ProductDomainModel {
    return ProductDomainModel(
        id = this.id,
        name = this.name,
        price = this.price,
        currency = this.currency,
        unit = this.unit,
        details = this.details,
        imageUri = this.imageUri,
        stock = ProductStockDomainModel(
            quantity = this.stock,
            cost = (this.stock * price), stock.toDomain()
        )
    )
}

fun List<StockRoomEntity>.toDomain(): List<ProductStockDomainModel.History> {
    return map { stockEntity ->
        ProductStockDomainModel.History(
            date = stockEntity.date,
            qty = stockEntity.amount.toDouble(),
            tittle = stockEntity.description
        )
    }
}

fun ProductDomainModel.toData(): ProductRoomEntity {
    return this.id?.let { id ->
        ProductRoomEntity(
            id = id,
            name = this.name ?: "",
            price = this.price ?: 0f,
            currency = this.currency ?: "",
            unit = this.unit ?: "",
            details = this.details,
            imageUri = this.imageUri ?: "",
            stock = this.stock.quantity
        )
    } ?: run {
        ProductRoomEntity(
            name = this.name ?: "",
            price = this.price ?: 0f,
            currency = this.currency ?: "",
            unit = this.unit ?: "",
            details = this.details,
            imageUri = this.imageUri ?: "",
            stock = this.stock.quantity
        )
    }

}