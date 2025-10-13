package com.imecatro.products.ui.catalog

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.products.ui.catalog.state.ExportProductsState
import com.imecatro.products.ui.list.mappers.toProductUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = CatalogViewModel.Factory::class)
class CatalogViewModel @AssistedInject constructor(
    @Assisted("ids") private val ids: Collection<Long>,
    private val productsRepository: ProductsRepository
) : BaseViewModel<ExportProductsState>(ExportProductsState.idle) {

    override fun onStart() {
        updateState { copy(isProcessingCatalog = true) }

        viewModelScope.launch {
            val productsSelected = withContext(Dispatchers.IO) {
                productsRepository.getProductsWithIds(ids.toList())
                    .toProductUiModel()
                    .groupBy { it.category }
            }

            updateState { copy(products = productsSelected) }
            updateState { copy(isProcessingCatalog = false, catalogReady = true) }
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("ids") ids: Collection<Long>
        ): CatalogViewModel
    }
}
