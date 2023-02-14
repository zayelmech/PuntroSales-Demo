package com.imecatro.products.ui.list.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.products.ui.list.mappers.toProductUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.uistate.ListProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository, //= ProductsRepositoryDummyImpl()
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _productsList: MutableStateFlow<List<ProductUiModel>> =
        MutableStateFlow(listOf())
    val productsList: StateFlow<List<ProductUiModel>> = _productsList.asStateFlow()

    private val _uiState: MutableStateFlow<ListProductsUiState> =
        MutableStateFlow(ListProductsUiState.Initialized)
    val uiState: StateFlow<ListProductsUiState> = _uiState.asStateFlow()

    fun getAllProducts() {
        viewModelScope.launch(iODispatcher) {
            try {
                productsRepository.getAllProducts().collectLatest { list ->
//                    delay(5000) //active in case you want to see the shimmer effect
                    _uiState.value = ListProductsUiState.Success(list.size)
                    Log.d("TAG", "getAllProducts: ${list.size}")
                    _productsList.emit(list.toProductUiModel())
                }
            } catch (e: Exception) {
                Log.d("TAG", "getAllProducts: ${e.message}")

                _uiState.value = ListProductsUiState.Error(e.message ?: "Unable to get data")
            }
        }
    }
}

