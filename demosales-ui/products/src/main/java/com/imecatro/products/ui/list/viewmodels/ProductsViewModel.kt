package com.imecatro.products.ui.list.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.ExportProductsCsvUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.CategoriesFilter
import com.imecatro.products.ui.list.uistate.OrderedFilterState
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.uistate.ExportProductsState
import com.imecatro.products.ui.list.uistate.checkElement
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl()
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val exportProductsCsvUseCase: ExportProductsCsvUseCase,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ListProductsUiState>(ListProductsUiState.idle) {

    private val _filtersState = MutableStateFlow(OrderedFilterState.filters)

    val filtersState: StateFlow<List<OrderedFilterState>> = _filtersState.asStateFlow()

    private val _categories: MutableStateFlow<List<CategoriesFilter>> =
        MutableStateFlow(emptyList())
    val categories: StateFlow<List<CategoriesFilter>> = _categories.asStateFlow()

    private val _reportState = MutableStateFlow(ExportProductsState())

    val reportState = _reportState.asStateFlow()


    val productsList: StateFlow<List<ProductUiModel>> =
        productsRepository.getAllProducts()
            .onStart {
                updateState { copy(isFetchingProducts = true) }
            }
            .combine(filtersState) { products, filters ->
                // Filter by order
                applyOrderFilter(products, filters)
            }
            .combine(categories) { products, categories ->
                applyCategories(products, categories)
            }
            .map { it.toProductUiModel() }
            .combine(reportState) { products, report ->
                val ids = report.ids
                products.map { product -> product.copy(isSelected = ids.contains(product.id)) }
            }
            .onEach {
                updateState { copy(isFetchingProducts = false) }
            }
            .flowOn(Dispatchers.Default) // Perform combine and map on Default dispatcher
            .catch {
                updateState { copy(isFetchingProducts = false) }

                val error = ErrorUiModel("", "No data")
                updateState { copy(errorFetchingProducts = error) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private fun applyOrderFilter(
        products: List<ProductDomainModel>, // Assuming products from repository are Domain models
        filters: List<OrderedFilterState>
    ): List<ProductDomainModel> {
        val orderSelected: OrderedFilterState? = filters.firstOrNull { it.isChecked }

        return orderSelected?.let { selectedFilter ->
            when (selectedFilter.type) {
                OrderedFilterState.Type.NAME -> products.sortedBy { it.name?.lowercase() }
                OrderedFilterState.Type.PRICE -> products.sortedBy { it.price }
                OrderedFilterState.Type.STOCK -> products.sortedBy { it.stock.quantity }
                OrderedFilterState.Type.DATE -> products.sortedBy { it.id } // Consider if 'id' is appropriate for date sorting
                OrderedFilterState.Type.NAME_INVERSE -> products.sortedByDescending { it.name?.lowercase() }
                OrderedFilterState.Type.PRICE_INVERSE -> products.sortedByDescending { it.price }
                OrderedFilterState.Type.STOCK_INVERSE -> products.sortedByDescending { it.stock.quantity }
                OrderedFilterState.Type.DATE_INVERSE -> products.sortedByDescending { it.id } // Same consideration for 'id'
            }
        } ?: products // Return original list if no filter is selected or if orderSelected is null
    }

    private fun applyCategories(
        products: List<ProductDomainModel>, // Assuming products from repository are Domain models
        filters: List<CategoriesFilter>
    ): List<ProductDomainModel> {
        val categoriesChecked = filters.filter { it.isChecked }

        return if (categoriesChecked.isNotEmpty()) {
            products.filter { product ->
                categoriesChecked.any { category ->
                    product.category?.name == category.text ||
                            category.text.isEmpty() && product.category == null // for products with no category

                }
            }
        } else {
            products
        }
    }


    fun onSearchAction(query: String) {
        viewModelScope.launch(iODispatcher) {

            val list = productsRepository.searchProducts(query).first()
            updateState { copy(productsFiltered = list.toProductUiModel()) }
        }

    }

    fun onFilterChange(filter: OrderedFilterState) {
        viewModelScope.launch(iODispatcher) {
            _filtersState.update { it.checkElement(filter) }
        }
    }

    fun onFilterCategory(category: CategoriesFilter) {
        viewModelScope.launch(iODispatcher) {
            _categories.update { lst ->
                val index = lst.indexOf(category)
                lst.mapIndexed { i, categoryFilter ->
                    if (i == index)
                        categoryFilter.copy(isChecked = !categoryFilter.isChecked)
                    else
                        categoryFilter
                }
            }
        }
    }

    override fun onStart() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesUseCase().collect { list ->
                val filter = list.map { CategoriesFilter(it.name, false) }

                val uncategorized = CategoriesFilter("", false) // for elements with no category
                _categories.update { filter.plus(uncategorized) }

            }
        }
    }

    fun onProductSelected(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        if (id == null) return@launch
        Log.d("onProductSelected", "onProductSelected: $id")
        if (_reportState.value.ids.contains(id))
            _reportState.update { it.copy(ids = it.ids.minus(id)) }
        else
            _reportState.update { it.copy(ids = it.ids.plus(id)) }

        // Uncheck selection
        if (productsList.value.size != _reportState.value.ids.size) {
            _reportState.update { it.copy(allSelected = false) }
        }

    }

    fun onSelectAllProducts(checked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _reportState.update { it.copy(allSelected = checked) }
        val currentIds: List<Long> = productsList.value.map { it.id ?: 0L }.filter { it != 0L }
        if (checked)
            _reportState.update { it.copy(ids = currentIds) }
        else
            _reportState.update { it.copy(ids = emptyList()) }

    }

    fun onClearSelections() = viewModelScope.launch(Dispatchers.IO) {
        _reportState.update { it.copy(ids = emptyList()) }
    }

    fun onProcessProducts() = viewModelScope.launch(Dispatchers.IO) {
        _reportState.update { it.copy(isProcessingCatalog = true) }
        val productsSelected = withContext(Dispatchers.Default) {
            productsList.value.filter { it.isSelected }.groupBy { it.category }
        }
        _reportState.update { it.copy(products = productsSelected) }
        _reportState.update { it.copy(isProcessingCatalog = false, productsReady = true) }

        exportProductsCsvUseCase.execute{
            ids = reportState.value.ids
        }.onSuccess { file ->
            _reportState.update { it.copy(catalogFile = file) }
        }.onFailure {
            Log.e(TAG, "onProcessProducts: ",it )
        }
    }

    fun onCatalogShared() {
        _reportState.update { it.copy(productsReady = false) }
        _reportState.update { it.copy(catalogFile = null) }
    }
}

private const val TAG = "ProductsViewModel"

