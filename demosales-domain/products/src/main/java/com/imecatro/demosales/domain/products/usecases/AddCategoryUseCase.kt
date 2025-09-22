package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.repository.CategoriesRepository

typealias AddCategoryInput = AddCategoryUseCase.Input.() -> Unit

/**
 * Use case for adding a new product category.
 *
 * This class handles the business logic for creating a new product category. It ensures that the
 * category name is not empty and that a category with the same name does not already exist.
 *
 * @param categoryRepository The repository for accessing and managing product categories.
 * @param coroutineProvider Provides coroutine dispatchers for background execution.
 */
class AddCategoryUseCase(
    private val categoryRepository: CategoriesRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<AddCategoryInput, ProductCategoryDomainModel>(coroutineProvider) {

    override suspend fun doInBackground(input: AddCategoryInput): ProductCategoryDomainModel {
        val name = Input().apply(input).name
        if (name.isBlank()) {
            throw Exception("Category name cannot be empty")
        }
        val category : ProductCategoryDomainModel? =
            try {
                categoryRepository.findCategoryByName(name)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        if (category != null) {
            throw Exception("Category already exists")
        }
        val id = categoryRepository.addCategory(ProductCategoryDomainModel(name = name))
        if (id == 0L) {
            throw Exception("Error adding category")
        }
        return ProductCategoryDomainModel(id, name)
    }


    data class Input(var name: String = "")
}