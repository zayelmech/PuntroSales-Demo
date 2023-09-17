package com.imecatro.demosales.ui.sales.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.sales.list.model.SaleOnListDomainModel
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SalesListViewModel @Inject constructor(
    private val getAllSalesUseCase: GetAllSalesUseCase
) : ViewModel() {


    val salesListUiState: StateFlow<SalesList> = flow {
        getAllSalesUseCase.invoke().onSuccess {
            emit(it.toUiModel())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


}

private fun List<SaleOnListDomainModel>.toUiModel(): SalesList {
    return map {
        SaleOnListUiModel(
            id = it.id,
            clientName = it.clientName,
            date = it.date,
            total = it.total,
            status = it.status.str
        )
    }
}
