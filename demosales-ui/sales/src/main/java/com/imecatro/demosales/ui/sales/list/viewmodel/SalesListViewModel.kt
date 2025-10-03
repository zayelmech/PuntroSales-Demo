package com.imecatro.demosales.ui.sales.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.list.mappers.toUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import com.imecatro.demosales.ui.sales.list.model.StatusFilterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SalesListViewModel"

@HiltViewModel
class SalesListViewModel @Inject constructor(
    getAllSalesUseCase: GetAllSalesUseCase
) : ViewModel() {

    private val _idsSelected = MutableStateFlow<List<Long>>(mutableListOf())

    private val idsSelected : StateFlow<List<Long>> = _idsSelected.asStateFlow()
    private val _statusFilterState: MutableStateFlow<List<StatusFilterUiModel>> =
        MutableStateFlow(emptyList())

    val statusFilterState: StateFlow<List<StatusFilterUiModel>> =
        _statusFilterState.onStart {
            getStatusFilterList()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val salesListUiState: StateFlow<SalesList> =
        getAllSalesUseCase.invoke()
            .map { sales -> sales.toUiModel() }
            .combine(statusFilterState) { sales, statusFilter ->
                // Filter by status
                val statusSelected: List<String> =
                    statusFilter.filter { it.isChecked }.map { it.text }

                if (statusSelected.isEmpty())
                    sales // By default show all sales
                else
                    sales.filter { sale -> sale.status in statusSelected }
            }
            .combine(idsSelected){ sales, ids ->
                sales.map { sale -> sale.copy(isSelected = ids.contains(sale.id)) }
            }
            .catch { Log.e(TAG, ": ", it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    private fun getStatusFilterList() {
        viewModelScope.launch(Dispatchers.IO) {
            val lst = OrderStatus.entries
            _statusFilterState.update { lst.map { StatusFilterUiModel(text = it.str) } }
        }
    }

    fun onStatusFilterChange(status: StatusFilterUiModel) {
        _statusFilterState.update { lst ->
            val index = lst.indexOf(status)
            lst.mapIndexed { i, statusFilterUiModel ->
                if (i == index)
                    statusFilterUiModel.copy(isChecked = !statusFilterUiModel.isChecked)
                else
                    statusFilterUiModel
            }
        }
    }

    fun onCardSelected(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_idsSelected.value.contains(id)){
                _idsSelected.update { list -> list.minus(id)}
            }else{
                _idsSelected.update { list -> list.plus(id)}
            }
        }
    }

    fun onClearSelections() {
        viewModelScope.launch(Dispatchers.IO) {
            _idsSelected.update { emptyList() }
        }
    }

}
