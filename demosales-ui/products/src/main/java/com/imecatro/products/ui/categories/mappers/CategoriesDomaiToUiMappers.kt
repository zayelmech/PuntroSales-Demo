package com.imecatro.products.ui.categories.mappers

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.products.ui.categories.model.CategoryUiModel


fun ProductCategoryDomainModel.toUi(): CategoryUiModel {
    return CategoryUiModel(
        id = id ?: 0L,
        name = name
    )
}