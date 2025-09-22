package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.model.ProductCategoryDomainModel
import com.imecatro.demosales.domain.products.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve all product categories from the repository.
 *
 * This use case interacts with the [CategoriesRepository] to fetch a flow
 * of all available [ProductCategoryDomainModel] instances.
 *
 * @param categoryRepository The repository responsible for providing category data.
 * @param coroutineProvider Provides the coroutine dispatcher for background execution.
 */
class GetAllCategoriesUseCase(
    private val categoryRepository: CategoriesRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, Flow<List<ProductCategoryDomainModel>>>(coroutineProvider) {

    operator fun invoke(): Flow<List<ProductCategoryDomainModel>> =
        categoryRepository.categories


    override suspend fun doInBackground(input: Unit): Flow<List<ProductCategoryDomainModel>> {
        return categoryRepository.categories
    }
}