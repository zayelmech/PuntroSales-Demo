package com.imecatro.demosales.ui.sales.add.viewmodel

import androidx.lifecycle.ViewModel
import com.imecatro.demosales.ui.sales.add.model.SaleChargeUiModel
import com.imecatro.demosales.ui.sales.add.model.SaleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val initCharges = SaleChargeUiModel(0.0, 0.0, 0.0, 0.0, 0.0)
    private val initialTicket = SaleUiModel("Guest", "", listOf(), initCharges, "default", "")
    val currentTicket: MutableStateFlow<SaleUiModel> = MutableStateFlow(initialTicket)


    private val _showEditDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _editDialogState: MutableStateFlow<EditDialogUiState> =
        MutableStateFlow(EditDialogUiState.Uninitialized)
    val editableState = _editDialogState.asStateFlow()

    fun onShowEditInputDialog(input: EditDialogUiState) {
        _showEditDialog.update { true }
        _editDialogState.update { input }

    }

    fun onEditDialogConfirmation(input: EditDialogUiState,new : String) {
        _showEditDialog.update { false }
        when (input) {
            is EditDialogUiState.Client -> {
                currentTicket.value =
                    currentTicket.value.apply { clientName = input.name }
            }

            is EditDialogUiState.Extra -> {
                currentTicket.update { it.copy(totals = it.totals.copy(extra = new.toDouble())) }
            }

            is EditDialogUiState.Shipping -> {
                currentTicket.update { it.copy(totals = it.totals.copy(shippingCost =new.toDouble())) }
            }

            EditDialogUiState.Uninitialized -> {}
        }
    }

    fun onNoteChangeAction(note: String) {
        currentTicket.update { it.copy(note = note) }
    }

}


sealed class EditDialogUiState(val txt: String) {
    object Uninitialized : EditDialogUiState("")
    data class Client(val name: String) : EditDialogUiState(name)

    //    data class Note(val text: String) : EditDialogUiState()
    data class Shipping(val cost: Double) : EditDialogUiState(cost.toString())

    //    data class Tax(val amount: Double) : EditDialogUiState()
    data class Extra(val amount: Double) : EditDialogUiState(amount.toString())
}

