package com.imecatro.products.ui.categories.mappers

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.products.ui.categories.model.CategoryUiModel


internal fun toCategoryUiModel(p: ProductCategoryDomainModel): CategoryUiModel {
    return CategoryUiModel(
        id = p.id ?: 0L,
        name = p.name
    )
}