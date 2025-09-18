package com.imecatro.products.data.mappers

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.products.data.model.CategoryRoomEntity
import com.imecatro.products.data.model.ProductAnCategoryDetailsRoomModel
import com.imecatro.products.data.model.ProductFullDetailsRoomModel
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity

fun List<ProductRoomEntity>.toProductsListDomain(): List<ProductDomainModel> {
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
            cost = (this.stock.toBigDecimal() * price.toBigDecimal()).toDouble(),
            history = stock.toStockListDomain()
        ),
        category = null
    )
}

fun List<StockRoomEntity>.toStockListDomain(): List<ProductStockDomainModel.History> {
    return map { stockEntity ->
        ProductStockDomainModel.History(
            date = stockEntity.date,
            qty = stockEntity.amount,
            tittle = stockEntity.description
        )
    }
}

internal fun List<CategoryRoomEntity>.toCategoryListDomain(): List<ProductCategoryDomainModel> {
    return map { it.toDomain() }
}
internal fun  CategoryRoomEntity.toDomain(): ProductCategoryDomainModel {
    return ProductCategoryDomainModel(id = this.id, name = this.name)
}

internal fun List<ProductAnCategoryDetailsRoomModel>.toProductListDomain() : List<ProductDomainModel> {
    return map {
        val temp = it.product.toDomain()
        temp.copy(category = it.category?.toDomain())
    }
}


fun ProductFullDetailsRoomModel.toDomain() : ProductDomainModel {
    val category = this.category?.toDomain()
    val stock = this.stock
    return product.toDomain(stock).copy(category = category)
}