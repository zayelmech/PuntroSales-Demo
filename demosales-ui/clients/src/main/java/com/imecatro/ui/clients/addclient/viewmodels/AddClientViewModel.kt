package com.imecatro.ui.clients.addclient.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.domain.clients.usecases.AddClientUseCase
import com.imecatro.ui.clients.addclient.mappers.mapToDomainClientModel
import com.imecatro.ui.clients.addclient.model.ClientUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val addClientUseCase: AddClientUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _client : MutableStateFlow<ClientUiModel> = MutableStateFlow(ClientUiModel())
    val client : StateFlow<ClientUiModel> = _client

    fun onSavedAction(addClient: ClientUiModel){
        viewModelScope.launch(coroutineDispatcher) {
            addClientUseCase.executeInBackground(addClient.mapToDomainClientModel())
        }
    }

}