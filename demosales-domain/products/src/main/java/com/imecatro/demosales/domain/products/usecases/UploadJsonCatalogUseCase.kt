package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository


/**
 *
 *
 */
class UploadJsonCatalogUseCase(
    private val catalogRepository: CatalogRepository,
    ioDispatcher: CoroutineProvider
) : BackgroundUseCase<String, String>(ioDispatcher) {


    override suspend fun doInBackground(input: String): String {
       return  ""
    }

}
