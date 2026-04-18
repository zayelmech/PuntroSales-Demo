package com.imecatro.demosales.ui.sales.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.sales.list.model.SalesMetricsDomainModel
import com.imecatro.demosales.domain.sales.list.usecases.ExportProductsFromSaleUseCase
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetSalesMetricsUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.list.mappers.toUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import com.imecatro.demosales.ui.sales.list.model.StatusFilterUiModel
import com.imecatro.demosales.ui.sales.list.model.toDomain
import com.imecatro.demosales.ui.sales.list.state.ExportSalesReportInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

private const val TAG = "SalesListViewModel"

@HiltViewModel
class SalesListViewModel @Inject constructor(
    private val getAllSalesUseCase: GetAllSalesUseCase,
    private val coroutineProvider: CoroutineProvider,
    private val exportSalesReportUseCase: ExportSalesReportUseCase,
    private val exportProductsFromSaleUseCase: ExportProductsFromSaleUseCase,
    private val getSalesMetricsUseCase: GetSalesMetricsUseCase,
) : ViewModel() {

    private val _reportState = MutableStateFlow(ExportSalesReportInput())

    val reportState: StateFlow<ExportSalesReportInput> = _reportState.asStateFlow()

    private val _statusFilterState: MutableStateFlow<List<StatusFilterUiModel>> =
        MutableStateFlow(StatusFilterUiModel.filters)

    val statusFilterState: StateFlow<List<StatusFilterUiModel>> = _statusFilterState.asStateFlow()

    val metrics: StateFlow<SalesMetricsDomainModel> = getSalesMetricsUseCase.invoke()
        .flowOn(coroutineProvider.io)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SalesMetricsDomainModel(emptyList(), emptyList(), emptyList())
        )

    val todayTotalAmount: StateFlow<Double> =
        getAllSalesUseCase.invoke()
            .map { sales ->
                val today = LocalDate.now()
                sales.filter { sale ->
                    sale.status == OrderStatus.COMPLETED &&
                            Instant.ofEpochMilli(sale.date)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() == today
                }.sumOf { it.total }
            }
            .catch { Log.e(TAG, "todayTotalAmount calculation error: ", it) }
            .flowOn(coroutineProvider.io)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val salesListUiState: StateFlow<SalesList> =
        getAllSalesUseCase.invoke()
            .map { sales -> sales.toUiModel() }
            .combine(statusFilterState) { sales, statusFilter ->
                // Filter by status
                val statusSelected: List<String> =
                    statusFilter.filter { it.isChecked }.map { it.toDomain() }

                if (statusSelected.isEmpty())
                    sales // By default, show all sales
                else
                    sales.filter { sale -> sale.status in statusSelected }
            }
            .combine(reportState) { sales, report ->
                val ids = report.ids
                sales.map { sale -> sale.copy(isSelected = ids.contains(sale.id)) }
            }
            .catch { Log.e(TAG, ": ", it) }
            .flowOn(coroutineProvider.io)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    fun onStatusFilterChange(status: StatusFilterUiModel) = viewModelScope.launch(coroutineProvider.io) {
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

    fun onCardSelected(id: Long) = viewModelScope.launch(coroutineProvider.io) {
        if (_reportState.value.ids.contains(id))
            _reportState.update { it.copy(ids = it.ids.minus(id)) }
        else
            _reportState.update { it.copy(ids = it.ids.plus(id)) }

        // Uncheck selection
        if (salesListUiState.value.size != _reportState.value.ids.size) {
            _reportState.update { it.copy(allSelected = false) }
        }
    }

    fun onClearSelections() = viewModelScope.launch(coroutineProvider.io) {
        _reportState.update { it.copy(ids = emptyList()) }
    }

    fun onDownloadCsv() = viewModelScope.launch(coroutineProvider.io) {
        launch {
            exportSalesReportUseCase.execute {
                ids = reportState.value.ids
            }.onSuccess { file ->
                _reportState.update { it.copy(salesFile = file) }
            }.onFailure { err ->
                Log.e(TAG, "onDownloadCsv: ", err)
            }
        }

        launch {
            exportProductsFromSaleUseCase.execute {
                ids = reportState.value.ids
            }.onSuccess { file ->
                _reportState.update { it.copy(groupedProductsFile = file) }
            }.onFailure { err ->
                Log.e(TAG, "onDownloadCsv: ", err)
            }
        }
    }

    fun onReportSent() {
        _reportState.update { it.copy(salesFile = null, groupedProductsFile = null) }
    }


    fun onSelectAllSales(checked: Boolean) = viewModelScope.launch(coroutineProvider.io) {
        _reportState.update { it.copy(allSelected = checked) }
        val currentIds = salesListUiState.value.map { it.id }
        if (checked)
            _reportState.update { it.copy(ids = currentIds) }
        else
            _reportState.update { it.copy(ids = emptyList()) }

    }

}
