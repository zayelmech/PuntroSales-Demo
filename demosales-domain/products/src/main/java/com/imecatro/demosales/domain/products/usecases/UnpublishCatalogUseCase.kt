package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository

/**
 * Use case to unpublish the current web catalog.
 */
class UnpublishCatalogUseCase(
    private val catalogRepository: CatalogRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, Unit>(coroutineProvider) {

    override suspend fun doInBackground(input: Unit) {
        catalogRepository.unpublishCatalog()
    }
}
