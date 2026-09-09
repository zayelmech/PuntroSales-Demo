package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository

/**
 * Use case to check if the current user has a published web catalog and retrieve it.
 */
class IsCatalogPublishedUseCase(
    private val catalogRepository: CatalogRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, WebCatalogDomainModel?>(coroutineProvider) {

    override suspend fun doInBackground(input: Unit): WebCatalogDomainModel? {
        return catalogRepository.getPublishedCatalog()
    }
}
