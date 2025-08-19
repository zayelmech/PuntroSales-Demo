package com.imecatro.demosales.ui.sales.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.sales.list.usecases.GetAllSalesUseCase
import com.imecatro.demosales.ui.sales.list.mappers.toUiModel
import com.imecatro.demosales.ui.sales.list.model.SalesList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "SalesListViewModel"

@HiltViewModel
class SalesListViewModel @Inject constructor(
    getAllSalesUseCase: GetAllSalesUseCase
) : ViewModel() {


    val salesListUiState: StateFlow<SalesList> =
        getAllSalesUseCase.invoke().map { it.toUiModel() }.catch {
            Log.e(TAG, ": ", it)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


}
