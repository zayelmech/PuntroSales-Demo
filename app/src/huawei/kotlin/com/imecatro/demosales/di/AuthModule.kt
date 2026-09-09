package com.imecatro.demosales.di

import com.imecatro.demosales.domain.core.auth.repository.AuthRepository
import com.imecatro.demosales.domain.core.auth.repository.UserAuthenticated
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.usecases.WebCatalogDomainModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val UNSUPPORTED_MESSAGE =
    "Web catalog publishing is not available in the Huawei distribution"

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = UnsupportedAuthRepository

    @Provides
    @Singleton
    fun provideCatalogRepository(): CatalogRepository = UnsupportedCatalogRepository
}

private data object UnsupportedAuthRepository : AuthRepository {
    override fun isUserAuthenticated(): Boolean = false

    override suspend fun signInWithGoogle(idToken: String): Result<UserAuthenticated> =
        Result.failure(UnsupportedOperationException(UNSUPPORTED_MESSAGE))

    override suspend fun signOut() = Unit

    override fun getCurrentUserDisplayName(): String? = null
}

private data object UnsupportedCatalogRepository : CatalogRepository {
    override suspend fun uploadImage(imageUri: String): String = unsupported()

    override suspend fun uploadJsonCatalog(catalog: WebCatalogDomainModel): String = unsupported()

    override suspend fun getCatalogWebViewerUrl(): String = ""

    override suspend fun createsFinalUrl(jsonCatalogUrl: String): String = unsupported()

    override suspend fun previewUrl(jsonCatalogUrl: String): String = unsupported()

    override suspend fun isCatalogPublished(): Boolean = false

    override suspend fun getPublishedCatalog(): WebCatalogDomainModel? = null

    override suspend fun unpublishCatalog(): Unit = unsupported()

    private fun <T> unsupported(): T = throw UnsupportedOperationException(UNSUPPORTED_MESSAGE)
}
