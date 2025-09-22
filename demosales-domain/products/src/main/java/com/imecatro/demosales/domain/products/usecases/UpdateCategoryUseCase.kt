package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.repository.CategoriesRepository


typealias UpdateCategoryInput = UpdateCategoryUseCase.Input.() -> Unit

/**
 * Use case for updating a product category in the repository.
 *
 * This use case takes an [UpdateCategoryInput] as input, which specifies the ID and new name
 * of the category to be updated. It then calls the `updateCategory` method of the
 * [CategoriesRepository] to perform the update operation.
 *
 * @param categoryRepository The repository for accessing and modifying category data.
 * @param coroutineProvider The provider for CoroutineDispatchers.
 */
class UpdateCategoryUseCase(
    private val categoryRepository: CategoriesRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<UpdateCategoryInput, Unit>(coroutineProvider) {

    override suspend fun doInBackground(input: UpdateCategoryInput) {
        val category = Input().apply(input)
        if (category.name.isBlank()){
            throw Exception("Category name cannot be empty")
        }
        categoryRepository.updateCategory(ProductCategoryDomainModel(category.id, category.name))
    }

    data class Input(
        var id: Long = 0,
        var name: String = ""
    )
}