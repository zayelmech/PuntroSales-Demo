package com.imecatro.demosales.domain.products.model

import com.imecatro.demosales.domain.core.model.ProductUnit


data class ProductDomainModel(
    var id: Int?,
    val name: String?,
    val price: Double?,
    val currency: String?,
    val unit: String? = ProductUnit.Default.symbol,
    val stock: ProductStockDomainModel,
    val details: String,
    val imageUri: String?
) {
    companion object {
        fun getProductUnitBySymbol(v: String?): ProductUnit = v.toProductUnit()
    }

}

private fun String?.toProductUnit(): ProductUnit {
    return ProductUnit.entries.find { it.symbol == this }
        ?: ProductUnit.Default
}

