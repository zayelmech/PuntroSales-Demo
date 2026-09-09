package com.imecatro.products.ui.catalog.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.usecase.onAny
import com.imecatro.demosales.domain.core.profile.repository.ProfileRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.CatalogForm
import com.imecatro.demosales.domain.products.usecases.PublishWebCatalogUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.catalog.state.ExportProductsState
import com.imecatro.products.ui.list.mappers.toProductUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CatalogViewModel.Factory::class)
class CatalogViewModel @AssistedInject constructor(
    @Assisted("ids") private val ids: Collection<Long>,
    private val productsRepository: ProductsRepository,
    private val publishWebCatalogUseCase: PublishWebCatalogUseCase,
    private val profileRepository: ProfileRepository
) : BaseViewModel<ExportProductsState>(
    ExportProductsState.idle.copy(
        ids = ids.toList(),
        selectedProductIds = ids.toSet()
    )
) {

    override fun onStart() {
        viewModelScope.launch {
            // Load ALL products from the repository to allow full selection control
            productsRepository.getAllProducts().collectLatest { productsList ->
                val productsUi = productsList.toProductUiModel()
                val productsByCategory = productsUi.groupBy { it.category }

                updateState {
                    copy(products = productsByCategory)
                }
            }
        }

        viewModelScope.launch {
            profileRepository.getProfile().collectLatest { profile ->
                updateState {
                    copy(
                        storeName = profile.storeName,
                        storeDescription = profile.storeDescription,
                        whatsapp = profile.whatsapp,
                        location = profile.location,
                        currency = profile.currency,
                    )
                }
            }
        }
    }

    fun onUpdateStoreInfo(
        name: String,
        description: String,
        whatsapp: String,
        location: String,
        template: String,
        enableStock: Boolean
    ) {
        updateState {
            copy(
                storeName = name,
                storeDescription = description,
                whatsapp = whatsapp,
                location = location,
                template = template,
                enableStock = enableStock
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val currentProfile = profileRepository.getProfile().first()
            profileRepository.updateProfile(
                currentProfile.copy(
                    storeName = name,
                    storeDescription = description,
                    whatsapp = whatsapp,
                    location = location,
                )
            )
        }
    }

    fun onToggleProductSelection(productId: Long) {
        updateState {
            val newSelected = if (productId in selectedProductIds) {
                selectedProductIds - productId
            } else {
                selectedProductIds + productId
            }
            copy(selectedProductIds = newSelected)
        }
    }

    fun onPublishCatalog() {
        val currentState = uiState.value

        // Ensure the user has selected at least one product
        if (currentState.selectedProductIds.isEmpty()) {
            updateState {
                copy(
                    isProcessingCatalog = false,
                    errorMessage = "Please select at least one product to publish."
                )
            }
            return
        }

        updateState { copy(isProcessingCatalog = true, errorMessage = null) }
        viewModelScope.launch {
            val c = CatalogForm(
                storeName = currentState.storeName,
                storeDescription = currentState.storeDescription,
                whatsAppNumber = currentState.whatsapp,
                location = currentState.location,
                currency = currentState.currency,
                template = currentState.template,
                enableStock = currentState.enableStock,
                productsIds = currentState.selectedProductIds.toList()
            )
            publishWebCatalogUseCase.execute(c)
                .onAny {
                    updateState { copy(isProcessingCatalog = false) }
                }
                .onSuccess { url ->
                    updateState { copy(catalogReady = true, catalogUrl = url.publicCatalogUrl, previewUrl = url.previewUrl) }
                }.onFailure { e ->
                    updateState { copy(errorMessage = e.message ?: "Failed to publish catalog") }
                }
        }
    }

    fun onCatalogUrlConsumed() {
        updateState { copy(catalogReady = false) }
    }

    fun onPublishErrorConsumed() {
        updateState { copy(errorMessage = null) }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("ids") ids: Collection<Long>
        ): CatalogViewModel
    }
}
