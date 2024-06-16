package com.imecatro.demosales.ui.clients.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.GetAllClientsUseCase
import com.imecatro.demosales.ui.clients.list.mappers.toUiModel
import com.imecatro.demosales.ui.clients.list.model.ClientsListPresenterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "ClientsListViewModel"
@HiltViewModel
class ClientsListViewModel @Inject constructor(
    private val getAllClientsUseCase: GetAllClientsUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientsListPresenterModel> =
        MutableStateFlow(ClientsListPresenterModel())

    val uiState: StateFlow<ClientsListPresenterModel> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isFetchingClients = true) }

            getAllClientsUseCase.execute(Unit).onSuccess { clientsFlow ->

                clientsFlow.collectLatest {
                    Log.e(TAG, ":$it ")
                    _uiState.update { currentState ->
                        currentState.copy(
                            clients = it.toUiModel(),
                            isFetchingClients = false
                        )
                    }
                }
            }.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        errors = it.message,
                        isFetchingClients = false
                    )
                }
            }
        }
    }
}

