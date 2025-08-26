package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.uistate.ListProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl()
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ListProductsUiState>(ListProductsUiState.idle) {

    val productsList: StateFlow<List<ProductUiModel>> =
        productsRepository.getAllProducts()
            .onStart {
                updateState { copy(isFetchingProducts = true) }
            }.catch {
                updateState { copy(isFetchingProducts = false) }

                val error = ErrorUiModel("", "No data")
                updateState { copy(errorFetchingProducts = error) }
            }.onEach {
                updateState { copy(isFetchingProducts = false) }
            }.map { it.toProductUiModel() }
            .flowOn(iODispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun onSearchAction(query: String) {
        viewModelScope.launch(iODispatcher) {

            val list = productsRepository.searchProducts(query).first()
            updateState { copy(productsFiltered = list.toProductUiModel()) }
        }

    }

}

