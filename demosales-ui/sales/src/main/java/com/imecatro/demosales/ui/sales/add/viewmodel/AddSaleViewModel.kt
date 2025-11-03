package com.imecatro.demosales.ui.sales.add.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.usecases.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.SearchProductByBarcode
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteTicketByIdUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetMostPopularProductsUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateTicketStatusUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.add.mappers.toAddSaleUi
import com.imecatro.demosales.ui.sales.add.mappers.toDomain
import com.imecatro.demosales.ui.sales.add.mappers.toListAddSaleUi
import com.imecatro.demosales.ui.sales.add.mappers.toUi
import com.imecatro.demosales.ui.sales.add.mappers.toUpdateQtyDomain
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

private const val TAG = "AddSaleViewModel"

@HiltViewModel(assistedFactory = AddSaleViewModel.Factory::class)
class AddSaleViewModel @AssistedInject constructor(
    @Assisted("lastSaleId") private val lastSaleId: Long,
    private val updateTicketStatusUseCase: UpdateTicketStatusUseCase,
    private val deleteTicketByIdUseCase: DeleteTicketByIdUseCase,

    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val updateProductOnCartUseCase: UpdateProductOnCartUseCase,
    private val getCartFlowUseCase: GetCartFlowUseCase,
    private val deleteProductOnCartUseCase: DeleteProductOnCartUseCase,

    private val getMostPopularProductsUseCase: GetMostPopularProductsUseCase,
    private val getProductsLikeUseCase: GetProductsLikeUseCase,
    private val searchProductByBarcode: SearchProductByBarcode,
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase,
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _results: MutableStateFlow<List<ProductResultUiModel>> =
        MutableStateFlow(emptyList())

    private fun fetchMostPopularProducts() {
        viewModelScope.launch {
            // For some reason product might not exist in database
            // we retrieve the most popular products from Orders table
            val topIds = getMostPopularProductsUseCase()

            // then we get products details from products table
            if (topIds.isEmpty()) return@launch
            val topProducts = topIds.map {
                val product = getProductDetailsByIdUseCase(it)
                product?.toAddSaleUi()
            }

            val filtered = topProducts.filterNotNull()
            _results.update { filtered }
        }
    }

    @Volatile
    private var _ticketId: Long = 0
    val ticketId get() = _ticketId

    val cartList: StateFlow<List<ProductOnCartUiModel>> = channelFlow {
        // edit mechanism
        val id: Long? = if (lastSaleId > 0L) lastSaleId else null

        getCartFlowUseCase(id).collectLatest { ticket ->
            _ticketId = ticket.id
            _ticketSubtotal.update { ticket.totals.subTotal.toString() }
            send(ticket.toUi())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    val productsFound: StateFlow<List<ProductResultUiModel>> = _results.onStart {
        fetchMostPopularProducts()
    }.combine(cartList) { results, cart ->
        // add cart qty to results
        results.map { result ->
            val productOnCart = cart.firstOrNull { it.product.id == result.id }
            val cartQty = productOnCart?.qty ?: 0.0
            result.copy(qty = cartQty)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _ticketSubtotal: MutableStateFlow<String> = MutableStateFlow("0.0")
    val ticketSubtotal: StateFlow<String> = _ticketSubtotal.asStateFlow()

    fun onSearchProductAction(query: String) {
        viewModelScope.launch(dispatcher) {
            getProductsLikeUseCase(query).collect { results ->
                _results.update { results.toListAddSaleUi() }
                Log.d(TAG, "onSearchAction: ${results.firstOrNull()?.name ?: "Nothing"}")
            }
        }
    }

    fun onAddProductToCartAction(product: ProductResultUiModel) {
        viewModelScope.launch(dispatcher) {
            // verify if products is already in cart
            val productOnCart = cartList.value.firstOrNull { it.product.id == product.id }
            if (productOnCart != null)
                updateProductOnCartUseCase.invoke(productOnCart.toUpdateQtyDomain(productOnCart.qty + 1))
            else {
                Log.d(TAG, "onAddProductToCartAction: ${product.imageUri}")
                addProductToCartUseCase.invoke(product.toDomain())
            }
        }
    }

    fun onDeleteProductFromTicketAction(id: Long) {
        viewModelScope.launch {
            deleteProductOnCartUseCase.invoke(id)
        }
    }

    /**
     * The purpose us to save current ticket as initialized, stock wont be deducted here, it will be deducted on pending or completed
     */
    fun onSaveTicketAction() {

        viewModelScope.launch(dispatcher) {
            val response = updateTicketStatusUseCase(_ticketId, OrderStatus.INITIALIZED)
            response.onSuccess {

            }
            response.onFailure {

            }


        }
    }

    fun onQtyValueChangeAtPos(product: ProductOnCartUiModel, newQty: String) {
        // Validate input newQty
        if (newQty.isEmpty()) return

        val qty = additional(product.qty, newQty)

        viewModelScope.launch {
            if (qty <= 0.0)
                deleteProductOnCartUseCase.invoke(product.orderId)
            else
                updateProductOnCartUseCase.invoke(product.toUpdateQtyDomain(qty))

            Log.d(TAG, "onQtyValueChangeAtPos: $product > $qty")
        }
    }

    fun onDeductProductWithId(id: Long) {
        viewModelScope.launch {
            val product: ProductOnCartUiModel =
                cartList.value.firstOrNull { it.product.id == id } ?: return@launch

            onQtyValueChangeAtPos(product, "-1")
        }
    }

    private fun additional(oldQty: Double, newQty: String): Double {
        if (newQty.startsWith('+')) return newQty.filter { it.isDigit() }.toFloat() + oldQty
        if (newQty.startsWith('-')) return oldQty - newQty.filter { it.isDigit() }.toFloat()
        return newQty.filter { it.isDigit() || it == '.' }.toDouble()
    }

    fun onCancelTicketAction() {
        viewModelScope.launch {
            deleteTicketByIdUseCase.invoke(_ticketId)
        }
    }

    fun onSearchBarcode(scannedBarcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchProductByBarcode
                .execute(scannedBarcode)
                .onSuccess { result ->
                    _results.update { listOf(result.toAddSaleUi()) }
                }.onFailure {
                    _results.update { emptyList() }
                }
        }


    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("lastSaleId") lastSaleId: Long
        ): AddSaleViewModel
    }

}



