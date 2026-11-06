package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.demosales.domain.products.model.ProductOrder
import com.imecatro.demosales.domain.products.repository.ProductsPagingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.ExportProductsCsvUseCase
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.domain.products.usecases.IsCatalogPublishedUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.CategoriesFilter
import com.imecatro.products.ui.list.uistate.ListProductsUiState
import com.imecatro.products.ui.list.uistate.OrderedFilterState
import com.imecatro.products.ui.list.uistate.checkElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val pagingRepository: ProductsPagingRepository,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val exportProductsCsvUseCase: ExportProductsCsvUseCase,
    private val isCatalogPublishedUseCase: IsCatalogPublishedUseCase,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ListProductsUiState>(ListProductsUiState.idle.copy(isFetchingProducts = false)) {

    private val _filtersState = MutableStateFlow(OrderedFilterState.filters)

    val filtersState: StateFlow<List<OrderedFilterState>> = _filtersState.asStateFlow()

    private val _categories: MutableStateFlow<List<CategoriesFilter>> =
        MutableStateFlow(emptyList())
    val categories: StateFlow<List<CategoriesFilter>> = _categories.asStateFlow()

    private val query = MutableStateFlow(ProductsQuery())
    private var selectionJob: Job? = null
    private var started = false

    // Cache prepared rows for this ViewModel, independently of selection/dialog state.
    val productsList = query
        // Preserve search's existing full-catalog, ID-ordered prefix results.
        .map { if (it.search.isEmpty()) it else ProductsQuery(search = it.search) }
        .distinctUntilChanged()
        .flatMapLatest { pagingRepository.getProductsPaged(it) }
        .map { page -> page.map { it.toProductUiModel() } }
        .cachedIn(viewModelScope)

    fun onSearchAction(text: String) {
        if (query.value.search == text) return
        selectionJob?.cancel()
        query.update { it.copy(search = text) }
    }

    fun onFilterChange(filter: OrderedFilterState) {
        _filtersState.update { it.checkElement(filter) }
        val order = when (filter.type) {
            OrderedFilterState.Type.NAME, OrderedFilterState.Type.NAME_INVERSE -> ProductOrder.NAME
            OrderedFilterState.Type.PRICE, OrderedFilterState.Type.PRICE_INVERSE -> ProductOrder.PRICE
            OrderedFilterState.Type.STOCK, OrderedFilterState.Type.STOCK_INVERSE -> ProductOrder.STOCK
            OrderedFilterState.Type.DATE, OrderedFilterState.Type.DATE_INVERSE -> ProductOrder.CREATED
        }
        // Preserve the existing enum-to-direction behavior, including price ordering.
        val descending = filter.type in setOf(OrderedFilterState.Type.NAME_INVERSE,
            OrderedFilterState.Type.PRICE_INVERSE, OrderedFilterState.Type.STOCK_INVERSE,
            OrderedFilterState.Type.DATE_INVERSE)
        selectionJob?.cancel()
        query.update { it.copy(order = order, descending = descending) }
    }

    fun onFilterCategory(category: CategoriesFilter) {
        _categories.update { list -> list.map { if (it == category) it.copy(isChecked = !it.isChecked) else it } }
        updateCategoryQuery()
    }

    private fun updateCategoryQuery() {
        val selected = _categories.value.filter { it.isChecked }
        val next = query.value.copy(categoryNames = selected.map { it.text }.toSet(),
            includeUncategorized = selected.any { it.text.isEmpty() })
        if (next == query.value) return
        selectionJob?.cancel()
        query.value = next
    }

    override fun onStart() {
        if (started) return
        started = true
        viewModelScope.launch {
            isCatalogPublishedUseCase.execute(Unit).onSuccess { catalog ->
                updateState { copy(isCatalogPublished = catalog != null) }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesUseCase().collect { list ->
                val checked = _categories.value.filter { it.isChecked }.map { it.text }.toSet()
                val filter = list.map { CategoriesFilter(it.name, it.name in checked) }

                val uncategorized = CategoriesFilter("", "" in checked) // for elements with no category
                _categories.update { filter.plus(uncategorized) }
                updateCategoryQuery()

            }
        }
    }

    fun onProductSelected(id: Long?) {
        selectionJob?.cancel()
        updateState {
            if (id == null) copy(enableSelection = true)
            else copy(idsSelected = if (id in idsSelected) idsSelected - id else idsSelected + id,
                allSelected = false)
        }
    }

    fun onSelectAllProducts(checked: Boolean) {
        selectionJob?.cancel()
        if (!checked) {
            updateState { copy(allSelected = false, idsSelected = emptyList()) }
            return
        }
        val selectedQuery = query.value.copy(search = "")
        selectionJob = viewModelScope.launch(iODispatcher) {
            // Read IDs for the complete result, including pages not loaded in the UI.
            try {
                val ids = pagingRepository.getMatchingIds(selectedQuery)
                updateState { copy(idsSelected = ids, allSelected = true, enableSelection = true) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                updateState { copy(errorFetchingProducts = ErrorUiModel("", error.message ?: "No data")) }
            }
        }
    }

    fun onClearSelections() {
        selectionJob?.cancel()
        updateState { copy(idsSelected = emptyList(), enableSelection = false, allSelected = false) }
    }

    fun onErrorConsumed() = updateState { copy(errorFetchingProducts = null) }

    fun generateSampleData() = viewModelScope.launch(iODispatcher) {
        val samples = listOf(
            ProductDomainModel(
                id = null,
                name = "Apple",
                price = 1.5,
                currency = "$",
                unit = "pz",
                stock = ProductStockDomainModel(10.0, 0.8, emptyList()),
                details = "Fresh red apples",
                imageUri = null,
                category = null,
                barcode = "123456789"
            ),
            ProductDomainModel(
                id = null,
                name = "Milk",
                price = 2.0,
                currency = "$",
                unit = "pz",
                stock = ProductStockDomainModel(5.0, 1.2, emptyList()),
                details = "Whole milk 1L",
                imageUri = null,
                category = null,
                barcode = "987654321"
            ),
            ProductDomainModel(
                id = null,
                name = "Bread",
                price = 1.0,
                currency = "$",
                unit = "pz",
                stock = ProductStockDomainModel(20.0, 0.5, emptyList()),
                details = "White bread",
                imageUri = null,
                category = null,
                barcode = "456789123"
            )
        )
        samples.forEach { productsRepository.addProduct(it) }
    }

    fun onProcessProducts() = viewModelScope.launch {
        updateState { copy(isProcessingCatalog = true) }

        exportProductsCsvUseCase.execute {
            ids = uiState.value.idsSelected
        }.onSuccess { file ->
            updateState { copy(catalogFile = file, isProcessingCatalog = false) }
        }.onFailure {
            updateState { copy(isProcessingCatalog = false) }
        }
    }

    fun onCatalogShared() {
        updateState { copy(catalogFile = null) }
    }
}

