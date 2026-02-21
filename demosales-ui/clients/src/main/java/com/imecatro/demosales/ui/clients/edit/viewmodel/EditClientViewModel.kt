package com.imecatro.demosales.ui.clients.edit.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.model.ClientDomainModel
import com.imecatro.demosales.domain.clients.usecases.GetClientDetailsByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateClientUseCase
import com.imecatro.demosales.ui.clients.edit.mappers.toDomain
import com.imecatro.demosales.ui.clients.edit.mappers.toUi
import com.imecatro.demosales.ui.clients.edit.model.EditClientUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditClientViewModel.Factory::class)
class EditClientViewModel @AssistedInject constructor(
    @Assisted("clientId") private val clientId: Long,
    private val getClientDetailsByIdUseCase: GetClientDetailsByIdUseCase,
    private val updateClientUseCase: UpdateClientUseCase
) : ViewModel() {


    private val _uiState: MutableStateFlow<EditClientUiModel> =
        MutableStateFlow(EditClientUiModel(0))

    val uiState: StateFlow<EditClientUiModel> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { cs -> cs.copy(isFetchingClientDetails = true) }
            getClientDetailsByIdUseCase.execute(clientId).onSuccess { details ->
                _uiState.update { cs -> cs.copy(isFetchingClientDetails = false) }
                _uiState.update { cs -> details.toUi(cs) }
            }.onFailure {
                _uiState.update { cs ->
                    cs.copy(
                        isFetchingClientDetails = false, error = it.message
                    )
                }
            }
        }
    }

    fun onSaveClientAction(client: EditClientUiModel) {
        viewModelScope.launch {
            _uiState.update { cs -> cs.copy(isEditingClient = true) }
            updateClientUseCase.execute(client.toDomain()).onSuccess {
                _uiState.update { cs -> cs.copy(isEditingClient = false, isClientEdited = true) }

            }.onFailure {
                Log.e(TAG, "onSaveClientAction: $it" )
                _uiState.update { cs ->
                    cs.copy(
                        isEditingClient = false, error = it.message
                    )
                }
            }
        }
    }

    fun onClientNameChange(clientName: String) =
        _uiState.update { cs -> cs.copy(clientName = clientName) }

    fun onPhoneNumberChange(phoneNumber: String) =
        _uiState.update { cs -> cs.copy(phoneNumber = phoneNumber) }

    fun onClientAddressChange(shippingAddress: String) =
        _uiState.update { cs -> cs.copy(clientAddress = shippingAddress) }

    fun onImageUriChangeAction(uri: Uri) =
        _uiState.update { cs -> cs.copy(imageUri = uri) }

    fun onClientLatLngChange(longitude: Double, latitude: Double) {
        _uiState.update { cs -> cs.copy(longitude = longitude, latitude = latitude) }
    }
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("clientId") clientId: Long
        ): EditClientViewModel
    }

}


private const val TAG = "EditClientViewModel"