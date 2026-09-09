package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository

/**
 * Use case to retrieve the final public URL for the web catalog.
 */
class GetCatalogUrlUseCase(
    private val catalogRepository: CatalogRepository,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, String>(coroutineProvider) {

    override suspend fun doInBackground(input: Unit): String {
        return catalogRepository.getCatalogWebViewerUrl()
    }
}
