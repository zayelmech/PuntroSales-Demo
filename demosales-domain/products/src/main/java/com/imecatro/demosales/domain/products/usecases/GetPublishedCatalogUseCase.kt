package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository

/**
 * Use case to retrieve the published web catalog.
 */
class GetPublishedCatalogUseCase(
    private val catalogRepository: CatalogRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, WebCatalogDomainModel?>(coroutineProvider) {

    override suspend fun doInBackground(input: Unit): WebCatalogDomainModel? {
        return catalogRepository.getPublishedCatalog()
    }
}
