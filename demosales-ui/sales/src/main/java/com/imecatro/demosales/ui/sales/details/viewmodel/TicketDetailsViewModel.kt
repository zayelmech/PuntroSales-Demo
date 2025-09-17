package com.imecatro.demosales.ui.sales.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.products.usecases.AddStockUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.UpdateSaleStatusUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.details.mappers.toUi
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TicketDetailsViewModel.Factory::class)
class TicketDetailsViewModel @AssistedInject constructor(
    @Assisted("saleId") private val saleId: Long,
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val updateSaleStatus: UpdateSaleStatusUseCase,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val addStockUseCase: AddStockUseCase,
) : ViewModel() {

    private val _sale: MutableStateFlow<TicketDetailsUiModel> =
        MutableStateFlow(TicketDetailsUiModel(list = listOf()))
    val sale: StateFlow<TicketDetailsUiModel> = _sale.onStart {
        onGetDetailsAction()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        TicketDetailsUiModel(list = emptyList())
    )

    private fun onGetDetailsAction() {
        viewModelScope.launch(Dispatchers.IO) {
            val details = getDetailsOfSaleByIdUseCase.invoke(saleId)
            _sale.update { details.toUi().copy(id = saleId) }
            getClientDetailsByIdUseCase.execute(details.clientId)
                .onSuccess { result ->
                    _sale.update { it.copy(client = result.toUi()) }
                }.onFailure {

                }
        }
    }

    fun onDeleteTicketAction() {
        viewModelScope.launch(Dispatchers.IO) {
            updateSaleStatus.invoke(saleId, OrderStatus.CANCEL)
            //re-add stock
            if (sale.value.status !== OrderStatus.INITIALIZED.str) {
                sale.value.list.forEach { product ->
                    // todo creates re-stock use case to avoid logic here
                    addStockUseCase(
                        reference = "Sale #${saleId} Cancelled",
                        productId = product.id,
                        amount = product.qty
                    )
                }
            }
            _sale.update { it.copy(allProductsWereRefunded = true) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("saleId") saleId: Long
        ): TicketDetailsViewModel
    }
}


