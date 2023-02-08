package com.imecatro.demosales.ui.sales.add.mappers

import android.net.Uri
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel

fun ProductDomainModel.toAddSaleUi(): ProductResultUiModel {
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