package com.imecatro.demosales.ui.clients.details.viewmodel

import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.GetPurchasesByClientIdUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateFavoriteStatusUseCase
import com.imecatro.demosales.domain.core.architecture.usecase.onAny
import com.imecatro.demosales.ui.clients.details.mappers.toUi
import com.imecatro.demosales.ui.clients.details.model.ClientDetailsUiModel
import com.imecatro.demosales.ui.clients.details.model.PurchaseUiModel
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *
 * This viewmodel is in charge of handling states and uses cases for the client details screen.
 *
 * @param clientId the id of the client to be displayed
 * @param getClientDetailsByIdUseCase use case to get the client details by id
 * @param deleteClientByIdUseCase use case to delete the client by id
 * @param getPurchasesByClientIdUseCase use case to get the purchases by client id
 * @param updateFavoriteStatusUseCase use case to update the favorite status of the client
 */
@HiltViewModel(assistedFactory = ClientDetailsViewModel.Factory::class)
class ClientDetailsViewModel @AssistedInject constructor(
    @Assisted private val clientId: Long,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val deleteClientByIdUseCase: DeleteClientByIdUseCase,
    private val getPurchasesByClientIdUseCase: GetPurchasesByClientIdUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
) : BaseViewModel<ClientDetailsUiModel>(ClientDetailsUiModel.idle) {


    override fun onStart() {
        loadClientDetails()
        viewModelScope.launch {
            getPurchasesByClientIdUseCase(clientId).collect { list ->
                updateState {
                    copy(purchases = list.map { p ->
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

    private fun loadClientDetails() = viewModelScope.launch {
        updateState { copy(isFetchingClientDetails = true) } // start loading
        getClientDetailsByIdUseCase.execute(clientId)
            .onAny { updateState { copy(isFetchingClientDetails = false) } }
            .onSuccess { details ->
                updateState { details.toUi(uiState.value) }
            }.onFailure { updateState { copy(error = it.message) } }
    }

    fun onDeleteClient(clientId: Long) = viewModelScope.launch {
        updateState { copy(isDeletingClient = true) }
        deleteClientByIdUseCase.execute(clientId)
            .onAny { updateState { copy(isDeletingClient = false) } } // stop loading on any failure or success
            .onSuccess { updateState { copy(isClientDeleted = true) } } // updating state to success
            .onFailure { err -> updateState { copy(error = err.message) } } // updating state to error

    }

    fun onToggleFavorite() = viewModelScope.launch {
        val newFavoriteStatus = !uiState.value.isFavorite
        updateState { copy(isTogglingFavorite = true) }
        val input = UpdateFavoriteStatusUseCase.Input(clientId, newFavoriteStatus)
        updateFavoriteStatusUseCase.execute(input)
            .onAny {
                updateState { copy(isTogglingFavorite = false) }
            }.onSuccess { loadClientDetails() }
            .onFailure { err -> updateState { copy(error = err.message) } }

    }

    @AssistedFactory
    interface Factory {
        fun create(clientId: Long): ClientDetailsViewModel
    }
}