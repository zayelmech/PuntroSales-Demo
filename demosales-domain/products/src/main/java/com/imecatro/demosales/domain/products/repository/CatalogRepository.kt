package com.imecatro.demosales.domain.products.repository

import com.imecatro.demosales.domain.products.usecases.WebCatalogDomainModel

interface CatalogRepository {

    /**
     * @return url of the image uploaded to the server
     */
    suspend fun uploadImage(imageUri: String): String

    /**
     * @return url of the catalog uploaded to the server
     */
    suspend fun uploadJsonCatalog(catalog: WebCatalogDomainModel): String

    /**
     * @return the final web catalog URL for the web viewer.
     */
    suspend fun getCatalogWebViewerUrl(): String

    suspend fun createsFinalUrl(jsonCatalogUrl: String): String

    suspend fun previewUrl(jsonCatalogUrl: String): String

    /**
     * Checks if a catalog is currently published for the user.
     */
    suspend fun isCatalogPublished(): Boolean

    /**
     * Retrieves the published catalog for the current user.
     */
    suspend fun getPublishedCatalog(): WebCatalogDomainModel?

    /**
     * Unpublishes the current catalog.
     */
    suspend fun unpublishCatalog()
}