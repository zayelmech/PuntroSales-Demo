package com.imecatro.demosales.ui.clients.add.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.AddClientUseCase
import com.imecatro.demosales.ui.clients.add.mappers.toDomain
import com.imecatro.demosales.ui.clients.add.model.AddClientUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val addClientUseCase: AddClientUseCase,
    // TODO (we might use di for error messages)
) : ViewModel() {

    private val _uiSate: MutableStateFlow<AddClientUiModel> =
        MutableStateFlow(AddClientUiModel())

    val uiState: StateFlow<AddClientUiModel> = _uiSate.asStateFlow()


    fun saveClient(client: AddClientUiModel) {
        viewModelScope.launch {
            _uiSate.update { c -> c.copy(isSavingClient = true) }
            addClientUseCase.execute(client.toDomain()).onSuccess {
                _uiSate.update { c -> c.copy(isSavingClient = false, isClientSaved = true) }
            }.onFailure {
                Log.e(TAG, "saveClient: $it" )
                _uiSate.update { c ->
                    c.copy(
                        isSavingClient = false,
                        error = "Error saving client"
                    )
                }
            }
        }
    }

    fun onFormStateChangedAction(formState: AddClientUiModel) {
        _uiSate.update { c ->
            c.copy(
                clientName = formState.clientName,
                phoneNumber = formState.phoneNumber,
                clientAddress = formState.clientAddress,
                imageUri = formState.imageUri
            )
        }
    }


}

private const val TAG = "AddClientViewModel"