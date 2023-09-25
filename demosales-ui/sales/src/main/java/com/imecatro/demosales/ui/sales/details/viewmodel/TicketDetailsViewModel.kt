package com.imecatro.demosales.ui.sales.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.sales.details.DeleteTicketByIdUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.details.SaleDetailsDomainModel
import com.imecatro.demosales.domain.sales.model.Order
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.details.mappers.toUi
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketDetailsViewModel @Inject constructor(
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val deleteTicketByIdUseCase : DeleteTicketByIdUseCase
) : ViewModel() {

    private val _sale: MutableStateFlow<TicketDetailsUiModel> =
        MutableStateFlow(TicketDetailsUiModel(listOf()))
    val sale: StateFlow<TicketDetailsUiModel> = _sale.asStateFlow()

    fun onGetDetailsAction(id: Long) {
        viewModelScope.launch {
            val details = getDetailsOfSaleByIdUseCase.invoke(id)
            _sale.update { details.toUi() }
        }
    }

    fun onDeleteTicketAction(id: Long){
        viewModelScope.launch {
            deleteTicketByIdUseCase.invoke(id)
        }
    }
}


