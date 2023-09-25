package com.imecatro.demosales.ui.sales.add.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.mappers.*
import com.imecatro.demosales.ui.sales.add.mappers.toCartUiModel
import com.imecatro.demosales.ui.sales.add.mappers.toListAddSaleUi
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.uistate.TicketUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.math.BigDecimal
import javax.inject.Inject

private const val TAG = "AddSaleViewModel"

@HiltViewModel
class AddSaleViewModel @Inject constructor(
    private val addNewSaleToDatabaseUseCase: AddNewSaleToDatabaseUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val getCartFlowUseCase: GetCartFlowUseCase,
    private val getProductsLikeUseCase: GetProductsLikeUseCase,
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _results: MutableStateFlow<List<ProductResultUiModel>> = MutableStateFlow(listOf())
    val productsFound: StateFlow<List<ProductResultUiModel>> = _results.asStateFlow()

    private val _ticketState: MutableStateFlow<TicketUiState> =
        MutableStateFlow(TicketUiState.Initialized)
    val ticketState: StateFlow<TicketUiState> = _ticketState.asStateFlow()


    val cartList: StateFlow<List<ProductOnCartUiModel>> = channelFlow {
        getCartFlowUseCase.invoke().collectLatest { ticket ->
            val list = ticket.productsList.map { order ->
                val product = getProductDetailsByIdUseCase(order.productId)
                order.toUi(product?.toAddSaleUi()!!  )
            }
            send(list)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _ticketSubtotal: MutableStateFlow<String> = MutableStateFlow("0.0")
    val ticketSubtotal: StateFlow<String> = _ticketSubtotal.asStateFlow()

    fun onSearchAction(query: String) {
        viewModelScope.launch(dispatcher) {
            getProductsLikeUseCase(query).collect { results ->
                _results.update { results.toListAddSaleUi() }
                Log.d(TAG, "onSearchAction: ${results.firstOrNull()?.name ?: "Nothing"}")
            }
        }
    }

    fun onAddProductToCartAction(product: ProductResultUiModel) {
        viewModelScope.launch(dispatcher) {
            addProductToCartUseCase.invoke(
                product.toDomain()

            )
        }.invokeOnCompletion {
            calculateSubtotal()
        }
    }

    fun onDeleteProductFromTicketAction(index: Int) {
        lista.removeAt(index)
        calculateSubtotal()
    }

    private val lista = mutableStateListOf<ProductOnCartUiModel>()
    fun onGetCacheTicketAction() {

        _ticketState.value = TicketUiState.OnCache(listOf())
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

    fun onSaveTicketAction() {

        val saleModelDomain: SaleDomainModel = cartList.value.toDomainModel().apply {
            date = System.currentTimeMillis().toString()
            total = _ticketSubtotal.value.toDouble()
        }
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

    fun onCheckedOutTickedAction() {

    }


    fun onQtyValueChangeAtPos(pos: Int, qtyValue: String) {
        val element = lista.elementAt(pos)
        //next block must evaluate the qty value in order to confirm is a float digit
        if (qtyValue.isNotEmpty() && qtyValue.count { it == '.' } <= 1 && qtyValue.contains(Regex("\\d"))) {
            try {
                lista[pos] =
                    element.copy(qty = qtyValue.toFloat()) //copy notify an update was made, this is needed because
                onCalculateSubtotalAtPos(pos, qtyValue.toFloat())
            } catch (e: NumberFormatException) {
                Log.d(TAG, "onQtyValueChangeAtPos: ${e.message}")
            }
        }
    }

    private fun onCalculateSubtotalAtPos(pos: Int, qtyValue: Float) {
        val element = lista.elementAt(pos)
        lista[pos] = element.apply {
            subtotal = (product.price?.toBigDecimal()
                ?: 0f.toBigDecimal()) * qtyValue.toBigDecimal()//product.price?.times(qtyValue)?.toBigDecimal() ?: 0f.toBigDecimal()
        }
        viewModelScope.launch(dispatcher) {

            // _cartList.emit(lista.apply { size })
            calculateSubtotal()

        }
    }

    private fun calculateSubtotal() {
        val total = lista.fold(BigDecimal(0)) { acc, value -> acc + value.subtotal }
        _ticketSubtotal.tryEmit(total.toString())
    }

    fun onQtyValueIncreaseAtPos(pos: Int, i: Int) {
        val element = lista.elementAt(pos)
        lista[pos] = element.copy(qty = element.qty + i)

        onCalculateSubtotalAtPos(pos, element.qty + i)
    }

    fun onNavigateToCheckout() {
        _ticketState.value = TicketUiState.Checkout
    }
}

private fun ProductResultUiModel.toDomain(): Order {
    return Order(
        productId = id ?: 0,
        productName = name ?: "",
        productPrice = price ?: 0f,
        1f
    )
}


private fun Order.toUi(product: ProductResultUiModel): ProductOnCartUiModel =
    ProductOnCartUiModel(
        product = product,
        qty = qty,
        subtotal = (qty * (product.price ?: 0f)).toBigDecimal()
    )

