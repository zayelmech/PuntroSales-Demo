package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    val categories: Flow<List<ProductCategoryDomainModel>>

    suspend fun addCategory(category: ProductCategoryDomainModel): Long

    suspend fun updateCategory(category: ProductCategoryDomainModel)

    suspend fun deleteCategoryById(categoryId: Long)
    suspend fun findCategoryByName(name: String): ProductCategoryDomainModel
}