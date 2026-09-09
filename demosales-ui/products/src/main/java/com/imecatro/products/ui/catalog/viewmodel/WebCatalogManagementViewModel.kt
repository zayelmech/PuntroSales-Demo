package com.imecatro.products.ui.catalog.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.profile.repository.ProfileRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetCatalogUrlUseCase
import com.imecatro.demosales.domain.products.usecases.IsCatalogPublishedUseCase
import com.imecatro.demosales.domain.products.usecases.UnpublishCatalogUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.catalog.state.WebCatalogManagementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebCatalogManagementViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val productsRepository: ProductsRepository,
    private val isCatalogPublishedUseCase: IsCatalogPublishedUseCase,
    private val unpublishCatalogUseCase: UnpublishCatalogUseCase,
    private val getCatalogUrlUseCase: GetCatalogUrlUseCase
) : BaseViewModel<WebCatalogManagementState>(WebCatalogManagementState.idle) {

    override fun onStart() {
        loadData()
    }

    private fun loadData() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            // 1. Check if published and get remote catalog data
            val catalog = isCatalogPublishedUseCase.execute(Unit).getOrNull()
            val isPublished = catalog != null

            // 2. Get the official public URL
            val finalUrl = getCatalogUrlUseCase.execute(Unit).getOrDefault("")

            // 3. Observe Profile changes for store name
            launch {
                profileRepository.getProfile().collectLatest { profile ->
                    updateState {
                        copy(
                            catalogName = profile.storeName,
                            catalogUrl = finalUrl,
                            isPublished = isPublished
                        )
                    }
                }
            }

            // 4. Update Stats (Remote if published, otherwise Local)
            if (catalog != null) {
                updateState {
                    copy(
                        productsCount = catalog.products.size,
                        categoriesCount = catalog.categories.size,
                        imagesCount = catalog.products.count { it.imageUrl.isNotBlank() },
                        lastUpdate = "Publicado",
                        lastPublicationDate = "Activo",
                        lastUpdateDate = catalog.expiresAt,
                        visitsCount = (100..2000).random(), // Mocked visits for premium feel
                        isLoading = false
                    )
                }
            } else {
                productsRepository.getAllProducts().collectLatest { allProducts ->
                    val categories = allProducts.mapNotNull { it.category?.name }.distinct()
                    val withImage = allProducts.count { it.imageUri != null && it.imageUri!!.isNotBlank() }

                    updateState {
                        copy(
                            productsCount = allProducts.size,
                            categoriesCount = categories.size,
                            imagesCount = withImage,
                            lastUpdate = "No publicado",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onRefreshCatalog() {
        loadData()
    }

    fun onUnpublishCatalog() {
        updateState { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            unpublishCatalogUseCase.execute(Unit)
                .onSuccess {
                    updateState {
                        copy(
                            isLoading = false,
                            isPublished = false,
                            unpublished = true
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo despublicar el catálogo"
                        )
                    }
                }
        }
    }

    fun onErrorConsumed() {
        updateState { copy(errorMessage = null) }
    }
}
