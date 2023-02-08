package com.imecatro.demosales.ui.sales.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import com.imecatro.demosales.ui.sales.add.mappers.toListAddSaleUi
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddSaleViewModel(
    private val addNewSaleToDatabaseUseCase: AddNewSaleToDatabaseUseCase,
    private val getProductsLikeUseCase: GetProductsLikeUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _results: MutableStateFlow<List<ProductResultUiModel>> = MutableStateFlow(listOf())
    val productsFound: StateFlow<List<ProductResultUiModel>> = _results.asStateFlow()


    val saveState: MutableStateFlow<Int> = MutableStateFlow(0)

    val cartList = mutableListOf<ProductDomainModel>()

    fun onSearchAction(query: String) {
        viewModelScope.launch(dispatcher) {
            getProductsLikeUseCase(query).collectLatest { results ->
                _results.value = results.toListAddSaleUi()
            }
        }
    }

    fun onAddProductToCartAction() {

    }

    fun onSaveTicketAction(saleModelDomain: SaleModelDomain) {
        viewModelScope.launch(dispatcher) {
            val response = addNewSaleToDatabaseUseCase(saleModelDomain)
            response.onSuccess {
                saveState.value = 1
            }
        }

    }
}