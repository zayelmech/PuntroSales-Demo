package com.imecatro.demosales.ui.sales.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.products.search.GetProductsLikeUseCase
import com.imecatro.demosales.domain.products.usecases.AddStockUseCase
import com.imecatro.demosales.domain.products.usecases.GetProductDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.RemoveFromStockUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddNewSaleToDatabaseUseCase
import com.imecatro.demosales.domain.sales.add.usecases.AddProductToCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetCartFlowUseCase
import com.imecatro.demosales.domain.sales.add.usecases.GetMostPopularProductsUseCase
import com.imecatro.demosales.domain.sales.add.usecases.UpdateProductOnCartUseCase
import com.imecatro.demosales.domain.sales.add.usecases.DeleteTicketByIdUseCase
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.mappers.*
import com.imecatro.demosales.ui.sales.add.mappers.toListAddSaleUi
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "AddSaleViewModel"

@HiltViewModel(assistedFactory = EditSaleViewModel.Factory::class)
class EditSaleViewModel  @AssistedInject constructor(
    @Assisted("ticketId") private val ticketId: Long,
    private val addNewSaleToDatabaseUseCase: AddNewSaleToDatabaseUseCase,
    private val deleteTicketByIdUseCase: DeleteTicketByIdUseCase,

    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val updateProductOnCartUseCase: UpdateProductOnCartUseCase,
    private val getCartFlowUseCase: GetCartFlowUseCase,
    private val deleteProductOnCartUseCase: DeleteProductOnCartUseCase,

    private val getMostPopularProductsUseCase: GetMostPopularProductsUseCase,
    private val getProductsLikeUseCase: GetProductsLikeUseCase,
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase,

//    private val addStockUseCase: AddStockUseCase,
//    private val removeFromStockUseCase: RemoveFromStockUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _results: MutableStateFlow<List<ProductResultUiModel>> =
        MutableStateFlow(emptyList())
    val productsFound: StateFlow<List<ProductResultUiModel>> = _results.onStart {
        fetchMostPopularProducts()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    val saleId get() = ticketId

    val cartList: StateFlow<List<ProductOnCartUiModel>> = channelFlow {
        getCartFlowUseCase(ticketId).collectLatest { ticket ->

            _ticketSubtotal.update { ticket.totals.subTotal.toString() }
            send(ticket.toUi())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

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
            addProductToCartUseCase.invoke(product.toDomain())
        }
    }

    fun onDeleteProductFromTicketAction(id: Long) {
        viewModelScope.launch {
            deleteProductOnCartUseCase.invoke(id)
        }
    }

//    private val lista = mutableStateListOf<ProductOnCartUiModel>()

    /**
     * todo modify
     */
    fun onSaveTicketAction() {

        val saleModelDomain: SaleDomainModel = cartList.value.toDomainModel().copy(
            date = System.currentTimeMillis().toString(),
        )
        viewModelScope.launch(dispatcher) {
            val response = addNewSaleToDatabaseUseCase(saleModelDomain)
            response.onSuccess {
               // _ticketState.value = TicketUiState.Saved
            }
            response.onFailure {
               // _ticketState.value = TicketUiState.Error(it.message ?: "Error on saving data")
            }

//            cartList.first().forEach { product ->
//                removeFromStockUseCase(
//                    reference = "Sale #$_ticketId",
//                    productId = product.product.id,
//                    amount = product.qty
//                )
//            }
        }
    }

    fun onQtyValueChangeAtPos(product: ProductOnCartUiModel, newQty: String) {
        val qty = additional(product.qty, newQty)

        viewModelScope.launch {
            updateProductOnCartUseCase.invoke(product.toUpdateQtyDomain(qty))
            Log.d(TAG, "onQtyValueChangeAtPos: $product > $qty")
        }
    }

    private fun additional(oldQty: Double, newQty: String): Double {
        if (newQty.startsWith('+')) return newQty.filter { it.isDigit() }.toFloat() + oldQty
        if (newQty.startsWith('-')) return oldQty - newQty.filter { it.isDigit() }.toFloat()
        return newQty.filter { it.isDigit() || it == '.' }.toDouble()
    }

    fun onCancelTicketAction() {
        viewModelScope.launch {
            deleteTicketByIdUseCase.invoke(ticketId)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("ticketId") ticketId: Long
        ): EditSaleViewModel
    }

}



