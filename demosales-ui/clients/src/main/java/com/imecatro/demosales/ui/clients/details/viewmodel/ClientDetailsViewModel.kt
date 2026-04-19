package com.imecatro.demosales.ui.clients.details.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetPurchasesByClientIdUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateFavoriteStatusUseCase
import com.imecatro.demosales.ui.clients.details.mappers.toUi
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel(assistedFactory = ClientDetailsViewModel.Factory::class)
class ClientDetailsViewModel @AssistedInject constructor(
    @Assisted private val clientId: Long,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val deleteClientByIdUseCase: DeleteClientByIdUseCase,
    private val getPurchasesByClientIdUseCase: GetPurchasesByClientIdUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
) : ViewModel() {


    private val _uiState:
            MutableStateFlow<ClientDetailsUiModel> = MutableStateFlow(ClientDetailsUiModel())
    val uiState: StateFlow<ClientDetailsUiModel> =
        _uiState.onStart { loadingState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ClientDetailsUiModel.dummy)

    private fun loadingState() {
        loadClientDetails()
        viewModelScope.launch {
            getPurchasesByClientIdUseCase(clientId).collect { list ->
                _uiState.update {
                    it.copy(purchases = list.map { p ->
                        PurchaseUiModel(
                            id = p.id,
                            purchaseNumber = p.purchaseNumber,
                            description = p.description,
                            amount = String.format(Locale.getDefault(), "$%.2f", p.amount),
                            date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                                Date(p.date)
                            )
                        )
                    })
                }
            }
        }
    }

    private fun loadClientDetails() {
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

    fun onDeleteClientAction(clientId: Long) {
        viewModelScope.launch {
            _uiState.update { cs -> cs.copy(isDeletingClient = true) }
            deleteClientByIdUseCase.execute(clientId).onSuccess {
                _uiState.update { cs -> cs.copy(isDeletingClient = false, isClientDeleted = true) }
            }.onFailure {
                Log.e(TAG, "onDeleteClientAction: $it")
                _uiState.update { cs ->
                    cs.copy(
                        isDeletingClient = false,
                        error = it.message
                    )
                }
            }
        }
    }

    fun onToggleFavorite() {
        viewModelScope.launch {
            val newFavoriteStatus = !_uiState.value.isFavorite
            updateFavoriteStatusUseCase.execute(
                UpdateFavoriteStatusUseCase.Input(clientId, newFavoriteStatus)
            ).onSuccess {
                loadClientDetails()
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(clientId: Long): ClientDetailsViewModel
    }
}

private const val TAG = "ClientDetailsViewModel"
