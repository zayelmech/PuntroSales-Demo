package com.imecatro.demosales.ui.sales.add.viewmodel

import androidx.lifecycle.ViewModel
import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val initCharges = SaleChargeUiModel(0.0, 0.0, 0.0, 0.0, 0.0)
    private val initialTicket = SaleUiModel("", "", listOf(), initCharges, "default", "")
    val currentTicket: MutableStateFlow<SaleUiModel> = MutableStateFlow(initialTicket)


    private val _showEditDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val editDialogState: MutableStateFlow<EditDialogUiState> =
        MutableStateFlow(EditDialogUiState.Uninitialized)


    fun onShowEditInputDialog(input: EditDialogUiState) {
        _showEditDialog.value = true
        editDialogState.value = input

    }

    fun onEditDialogConfirmation(input: EditDialogUiState) {
        _showEditDialog.value = false
        when (input) {
            is EditDialogUiState.Client -> {
                currentTicket.value =
                    currentTicket.value.apply { clientName = input.name }
            }
            is EditDialogUiState.Note -> {
                currentTicket.value = currentTicket.value.apply { note = input.text }
            }
            is EditDialogUiState.Extra -> {
                currentTicket.value =
                    currentTicket.value.apply { totals.extra = input.amount }
                //TODO toString transformations requires try and catch block
            }
            is EditDialogUiState.Shipping -> {
                currentTicket.value = currentTicket.value.apply {
                    totals.shippingCost =
                        input.cost
                }
            }
            is EditDialogUiState.Tax -> {
                currentTicket.value = currentTicket.value.apply { totals.tax = input.amount }
            }
            EditDialogUiState.Uninitialized -> TODO()
        }
    }

}


sealed class EditDialogUiState {
    object Uninitialized : EditDialogUiState()
    data class Client(val name: String) : EditDialogUiState()
    data class Note(val text: String) : EditDialogUiState()
    data class Shipping(val cost: Double) : EditDialogUiState()
    data class Tax(val amount: Double) : EditDialogUiState()
    data class Extra(val amount: Double) : EditDialogUiState()
}

