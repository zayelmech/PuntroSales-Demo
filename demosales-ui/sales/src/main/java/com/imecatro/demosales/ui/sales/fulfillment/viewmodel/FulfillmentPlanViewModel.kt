package com.imecatro.demosales.ui.sales.fulfillment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.products.usecases.GetAllProductsUseCase
import com.imecatro.demosales.domain.sales.details.GetDetailsOfSaleByIdUseCase
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentPlanUiState
import com.imecatro.demosales.ui.sales.fulfillment.model.FulfillmentSale
import com.imecatro.demosales.ui.sales.fulfillment.model.buildFulfillmentPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FulfillmentPlanViewModel @Inject constructor(
    getAllSales: GetAllSalesUseCase,
    getAllProducts: GetAllProductsUseCase,
    getSaleDetails: GetDetailsOfSaleByIdUseCase,
    coroutineProvider: CoroutineProvider
) : ViewModel() {
    private val refreshRequests = MutableStateFlow(0)

    val uiState = refreshRequests.flatMapLatest {
        flow {
            emit(FulfillmentPlanUiState())
            emitAll(combine(getAllSales(), getAllProducts()) { sales, products ->
                val pending = sales.filter { it.status == OrderStatus.PENDING }.map { sale ->
                    FulfillmentSale(sale, getSaleDetails(sale.id))
                }
                buildFulfillmentPlan(pending, products)
            })
        }.catch {
            // Never present a partial plan as an exact purchasing list.
            emit(FulfillmentPlanUiState(isLoading = false, hasError = true))
        }
    }.flowOn(coroutineProvider.io).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FulfillmentPlanUiState()
    )

    fun refresh() {
        refreshRequests.update { it + 1 }
    }
}
