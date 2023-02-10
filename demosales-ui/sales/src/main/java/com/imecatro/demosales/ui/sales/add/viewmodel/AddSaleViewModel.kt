package com.imecatro.demosales.ui.sales.add.viewmodel

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.model.SaleModelDomain
import com.imecatro.demosales.ui.sales.add.mappers.SaleDomainToListProductOnCartUiMapper
import com.imecatro.demosales.ui.sales.add.mappers.toCartUiModel
import com.imecatro.demosales.ui.sales.add.mappers.toListAddSaleUi
import com.imecatro.demosales.ui.sales.add.mappers.toOrderDomainModel
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.uistate.TicketUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import javax.inject.Inject

private const val TAG = "AddSaleViewModel"

@HiltViewModel
class AddSaleViewModel @Inject constructor(
    private val addNewSaleToDatabaseUseCase: AddNewSaleToDatabaseUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val getCartFlowUseCase: GetCartFlowUseCase,
    private val saleDomainToListProductOnCartUiMapper: SaleDomainToListProductOnCartUiMapper,
    private val getProductsLikeUseCase: GetProductsLikeUseCase,
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _results: MutableStateFlow<List<ProductResultUiModel>> = MutableStateFlow(listOf())
    val productsFound: StateFlow<List<ProductResultUiModel>> = _results.asStateFlow()

//    val observer = getCartFlowUseCase().onEach {
//        _cartList.emit(saleDomainToListProductOnCartUiMapper.toUiModel(it))
//    }

    private val _ticketState: MutableStateFlow<TicketUiState> =
        MutableStateFlow(TicketUiState.Initialized)
    val ticketState: StateFlow<TicketUiState> = _ticketState.asStateFlow()

    private val _cartList: MutableStateFlow<List<ProductOnCartUiModel>> =
        MutableStateFlow(listOf())
    val cartList: StateFlow<List<ProductOnCartUiModel>> = _cartList.asStateFlow()

    fun onSearchAction(query: String) {
        viewModelScope.launch(dispatcher) {
            getProductsLikeUseCase(query).collect { results ->
                _results.emit(results.toListAddSaleUi())
                Log.d(TAG, "onSearchAction: ${results.firstOrNull()?.name ?: "Nothing"}")
            }
        }
    }

    fun onAddProductToCartAction(id: Int) {
        Log.d(TAG, "onAddProductToCartAction: $id")

        viewModelScope.launch(dispatcher) {

            getProductDetailsByIdUseCase(id)?.let {
                val p = it.toCartUiModel()
                lista.add(p)
                _cartList.emit(lista)

                //addProductToCartUseCase(p.toOrderDomainModel())
            }
            Log.d(TAG, "onAddProductToCartAction: ADDED")

        }.invokeOnCompletion {
            //onGetCacheTicketAction() //Uncomment in case data is not being update
        }
    }

    private val lista = mutableStateListOf<ProductOnCartUiModel>()
    fun onGetCacheTicketAction() {

        viewModelScope.launch(dispatcher) {
//            observer.collect {
//                val productsOnCart = saleDomainToListProductOnCartUiMapper.toUiModel(it)
//                //TODO handling error
//                Log.d(TAG, "onGetCacheTicketAction: ${productsOnCart.size}")
//                _cartList.emit(productsOnCart)
//                _ticketState.value = TicketUiState.OnCache(productsOnCart)
//            }
        }
    }

    fun onSaveTicketAction(saleModelDomain: SaleModelDomain) {
        viewModelScope.launch(dispatcher) {
            val response = addNewSaleToDatabaseUseCase(saleModelDomain)
            response.onSuccess {
                _ticketState.value = TicketUiState.Saved
            }
            response.onFailure {
                _ticketState.value = TicketUiState.Error(it.message ?: "Error on saving data")
            }
        }

    }


    fun onQtyValueChangeAtPos(pos: Int, qtyValue: Float) {
        val element = lista.elementAt(pos)
        lista[pos] = element.copy(qty = qtyValue) //copy notify an update was made, this is needed because

//            element.apply {
//            qty = qtyValue
//
//        }
        onCalculateSubtotalAtPos(pos,qtyValue)
    }

    private fun onCalculateSubtotalAtPos(pos: Int, qtyValue: Float) {
        val element = lista.elementAt(pos)
        lista[pos] = element.apply {
            subtotal = product.price?.times(qtyValue) ?: 10f
        }
        viewModelScope.launch(dispatcher) {
//            _cartList.tryEmit(lista.apply { add(element);remove(element)})

            _cartList.tryEmit(lista.apply { size})
        }
    }

    fun onQtyValueIncreaseAtPos(pos: Int, i: Int) {
        val element = lista.elementAt(pos)
        lista[pos] = element.copy(qty = element.qty+i)

//            element.apply {
//            qty += i
//        }
        onCalculateSubtotalAtPos(pos,element.qty)
    }
}
