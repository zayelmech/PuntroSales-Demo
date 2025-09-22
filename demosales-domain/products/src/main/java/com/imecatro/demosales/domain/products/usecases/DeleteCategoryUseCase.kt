package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CategoriesRepository


typealias DeleteCategoryInput = DeleteCategoryUseCase.Input.() -> Unit


/**
 * Use case for deleting a category.
 *
 * This class handles the business logic for deleting a category from the system.
 * It can delete a category by its ID or by its name.
 *
 * @param categoryRepository The repository for accessing category data.
 * @param coroutineProvider Provides coroutine dispatchers for background execution.
 */
class DeleteCategoryUseCase(
    private val categoryRepository: CategoriesRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<DeleteCategoryInput, Unit>(coroutineProvider) {

    override suspend fun doInBackground(input: DeleteCategoryInput) {
        val category = Input().apply(input)
        if (category.id > 0L)
            categoryRepository.deleteCategoryById(category.id)

        if (category.id == 0L && category.name.isNotBlank()) {
            val id = categoryRepository.findCategoryByName(category.name).id

            if (id != null) {
                categoryRepository.deleteCategoryById(id)
            } else {
                throw Exception("Category not found")
            }
        }
    }

    data class Input(
        var id: Long = 0L,
        var name: String = ""
    )
}