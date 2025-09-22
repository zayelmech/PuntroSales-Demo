package com.imecatro.products.data.repository

import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import com.imecatro.products.data.datasource.CategoriesDao
import com.imecatro.products.data.mappers.toCategoryListDomain
import com.imecatro.products.data.model.CategoryRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriesRepositoryImpl(private val categoriesDao: CategoriesDao) : CategoriesRepository {

    override val categories: Flow<List<ProductCategoryDomainModel>>
        get() = categoriesDao.getAll().map { it.toCategoryListDomain() }

    override suspend fun addCategory(category: ProductCategoryDomainModel): Long {
        if (category.name.isBlank()) return 0L
        return categoriesDao.insertCategory(CategoryRoomEntity(name = category.name))
    }

    override suspend fun updateCategory(category: ProductCategoryDomainModel) {
        if (category.id == null) return
        categoriesDao.updateCategory(CategoryRoomEntity(id = category.id!!, name = category.name))
    }

    override suspend fun deleteCategoryById(categoryId: Long) {
        categoriesDao.clearCategory(categoryId)
        categoriesDao.deleteCategory(categoryId)
    }

    override suspend fun findCategoryByName(name: String): ProductCategoryDomainModel {
        val category = categoriesDao.getCategoryByName(name)
        if (category == null) throw Exception("Category not found")

        return ProductCategoryDomainModel(name = category.name, id = category.id)
    }


}