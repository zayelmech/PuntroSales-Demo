package com.imecatro.demosales.ui.clients.details.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.ui.clients.details.mappers.toUi
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ClientDetailsViewModel.Factory::class)
class ClientDetailsViewModel @AssistedInject constructor(
    @Assisted private val clientId: Int,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val deleteClientByIdUseCase: DeleteClientByIdUseCase
) : ViewModel() {


    private val _uiState:
            MutableStateFlow<ClientDetailsUiModel> = MutableStateFlow(ClientDetailsUiModel())
    val uiState: StateFlow<ClientDetailsUiModel> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            _uiState.update { cs -> cs.copy(isFetchingClientDetails = true) }
            getClientDetailsByIdUseCase.execute(clientId).onSuccess { details ->
                _uiState.update { cs -> cs.copy(isFetchingClientDetails = false) }
                _uiState.update { cs -> details.toUi(cs) }
            }.onFailure {
                _uiState.update { cs ->
                    cs.copy(
                        isFetchingClientDetails = false,
                        error = it.message
                    )
                }
            }
        }
    }

    fun onDeleteClientAction(clientId: Int) {
        viewModelScope.launch {
            _uiState.update { cs -> cs.copy(isDeletingClient = true) }
            deleteClientByIdUseCase.execute(clientId).onSuccess {
                _uiState.update { cs -> cs.copy(isDeletingClient = false, isClientDeleted = true) }
            }.onFailure {
                Log.e(TAG, "onDeleteClientAction: $it" )
                _uiState.update { cs ->
                    cs.copy(
                        isDeletingClient = false,
                        error = it.message
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(clientId: Int): ClientDetailsViewModel
    }
}

private const val TAG = "ClientDetailsViewModel"
