package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.OrderedFilterUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.model.checkElement
import com.imecatro.products.ui.list.uistate.ListProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl()
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ListProductsUiState>(ListProductsUiState.idle) {

    private val _filtersState: MutableStateFlow<List<OrderedFilterUiModel>> =
        MutableStateFlow(OrderedFilterUiModel.filters)

    val filtersState: StateFlow<List<OrderedFilterUiModel>> = _filtersState.asStateFlow()

    val productsList: StateFlow<List<ProductUiModel>> =
        productsRepository.getAllProducts()
            .onStart {
                updateState { copy(isFetchingProducts = true) }
            }.onEach {
                updateState { copy(isFetchingProducts = false) }
            }.map { it.toProductUiModel() }
            .combine(filtersState) { products, filters ->
                // Filter by order

                val orderSelected: OrderedFilterUiModel = filters.first { it.isChecked }

                when (orderSelected.type) {
                    OrderedFilterUiModel.Type.NAME -> products.sortedBy { filter -> filter.name?.lowercase() }
                    OrderedFilterUiModel.Type.PRICE -> products.sortedBy { filter -> filter.price }
                    OrderedFilterUiModel.Type.STOCK -> products.sortedBy { filter -> filter.stock }
                    OrderedFilterUiModel.Type.DATE -> products.sortedBy { filter -> filter.id }
                    OrderedFilterUiModel.Type.NAME_INVERSE -> products.sortedByDescending { filter -> filter.name?.lowercase() }
                    OrderedFilterUiModel.Type.PRICE_INVERSE -> products.sortedByDescending { filter -> filter.price }
                    OrderedFilterUiModel.Type.STOCK_INVERSE -> products.sortedByDescending { filter -> filter.stock }
                    OrderedFilterUiModel.Type.DATE_INVERSE -> products.sortedByDescending { filter -> filter.id }
                }
            }
            .catch {
                updateState { copy(isFetchingProducts = false) }

                val error = ErrorUiModel("", "No data")
                updateState { copy(errorFetchingProducts = error) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun onSearchAction(query: String) {
        viewModelScope.launch(iODispatcher) {

            val list = productsRepository.searchProducts(query).first()
            updateState { copy(productsFiltered = list.toProductUiModel()) }
        }

    }

    fun onFilterChange(filter: OrderedFilterUiModel) {
        viewModelScope.launch(iODispatcher) {
            _filtersState.update { it.checkElement(filter) }
        }
    }

}

