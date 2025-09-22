package com.imecatro.products.ui.list.viewmodels

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.usecases.GetAllCategoriesUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import com.imecatro.demosales.ui.theme.architect.ErrorUiModel
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.CategoriesFilter
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
import kotlinx.coroutines.flow.flowOn
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
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ListProductsUiState>(ListProductsUiState.idle) {

    private val _filtersState: MutableStateFlow<List<OrderedFilterUiModel>> =
        MutableStateFlow(OrderedFilterUiModel.filters)

    val filtersState: StateFlow<List<OrderedFilterUiModel>> = _filtersState.asStateFlow()

    private val _categories: MutableStateFlow<List<CategoriesFilter>> =
        MutableStateFlow(emptyList())
    val categories: StateFlow<List<CategoriesFilter>> = _categories.asStateFlow()

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
        filters: List<OrderedFilterUiModel>
    ): List<ProductDomainModel> {
        val orderSelected: OrderedFilterUiModel? = filters.firstOrNull { it.isChecked }

        return orderSelected?.let { selectedFilter ->
            when (selectedFilter.type) {
                OrderedFilterUiModel.Type.NAME -> products.sortedBy { it.name?.lowercase() }
                OrderedFilterUiModel.Type.PRICE -> products.sortedBy { it.price }
                OrderedFilterUiModel.Type.STOCK -> products.sortedBy { it.stock.quantity }
                OrderedFilterUiModel.Type.DATE -> products.sortedBy { it.id } // Consider if 'id' is appropriate for date sorting
                OrderedFilterUiModel.Type.NAME_INVERSE -> products.sortedByDescending { it.name?.lowercase() }
                OrderedFilterUiModel.Type.PRICE_INVERSE -> products.sortedByDescending { it.price }
                OrderedFilterUiModel.Type.STOCK_INVERSE -> products.sortedByDescending { it.stock.quantity }
                OrderedFilterUiModel.Type.DATE_INVERSE -> products.sortedByDescending { it.id } // Same consideration for 'id'
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
                    product.category?.name == category.text
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

    fun onFilterChange(filter: OrderedFilterUiModel) {
        viewModelScope.launch(iODispatcher) {
            _filtersState.update { it.checkElement(filter) }
        }
    }

    fun onFilterCategory(category : CategoriesFilter) {
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
                _categories.update { filter }
            }
        }
    }

}

