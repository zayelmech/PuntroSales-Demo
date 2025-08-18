package com.imecatro.demosales.ui.sales.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.sales.add.usecases.CheckoutSaleUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getDetailsOfSaleByIdUseCase: GetDetailsOfSaleByIdUseCase,
    private val saveSaleUseCase: CheckoutSaleUseCase,

    ) : ViewModel() {


    private val initCharges = SaleChargeUiModel(0.0, 0.0, 0.0, 0.0, 0.0)
    private val initialTicket = SaleUiModel(0, "Guest", "", listOf(), initCharges, "default", "")
    val currentTicket: MutableStateFlow<SaleUiModel> = MutableStateFlow(initialTicket)

    fun onNoteChangeAction(note: String) {
        currentTicket.update { it.copy(note = note) }
    }

    fun onGetDetailsAction(saleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ticket = getDetailsOfSaleByIdUseCase(saleId)
            currentTicket.update {
                it.copy(
                    id = saleId,
                    totals = initCharges.copy(subtotal = ticket.total)
                )
            }
        }
    }

    /**
     * @param extra is the amount extra that must be added to the total
     */
    fun onExtraChargeAdded(extra: String) {
        //todo validates extra amount
        currentTicket.update { it.copy(totals = it.totals.copy(extra = extra.toDouble())) }
        currentTicket.update { it.copy(totals = it.totals.copy(total = extra.toDouble() + it.totals.subtotal)) }
    }


    fun onCheckoutAction() {
        viewModelScope.launch(Dispatchers.IO) {
            saveSaleUseCase(currentTicket.value.id) {
                note = currentTicket.value.note
                date = System.currentTimeMillis().toString()
                totals = currentTicket.value.totals.toDomain()
            }
            currentTicket.update { it.copy(ticketSaved = true) }
        }
    }

}

/**
 * @return date formatted 2025-08-17T21:05:42Z
 *
 */
fun nowIsoString(): String {
    return ZonedDateTime.now(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

private fun SaleChargeUiModel.toDomain(): SaleDomainModel.Costs {
    return SaleDomainModel.Costs(
        extraCost = this.extra,
        subTotal = this.subtotal,
        total = this.total
    )
}
