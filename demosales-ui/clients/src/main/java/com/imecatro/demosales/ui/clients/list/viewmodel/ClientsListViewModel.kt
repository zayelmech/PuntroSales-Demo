package com.imecatro.demosales.ui.clients.list.viewmodel

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imecatro.demosales.domain.clients.usecases.AddClientUseCase
import com.imecatro.demosales.domain.clients.usecases.GetAllClientsUseCase
import com.imecatro.demosales.domain.clients.usecases.GetClientByPhoneNumberUseCase
import com.imecatro.demosales.domain.clients.usecases.UpdateClientUseCase
import com.imecatro.demosales.ui.clients.list.mappers.toDomain
import com.imecatro.demosales.ui.clients.list.mappers.toUiModel
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel
import com.imecatro.demosales.ui.clients.list.model.ClientsListPresenterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


private const val TAG = "ClientsListViewModel"

@HiltViewModel
class ClientsListViewModel @Inject constructor(
    private val getAllClientsUseCase: GetAllClientsUseCase,
    private val addClientUseCase: AddClientUseCase,
    private val updateClientUseCase: UpdateClientUseCase,
    private val getClientByPhoneNumberUseCase: GetClientByPhoneNumberUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ClientsListPresenterModel> =
        MutableStateFlow(ClientsListPresenterModel())

    val uiState: StateFlow<ClientsListPresenterModel> = _uiState.asStateFlow()

    private val _contacts: MutableStateFlow<List<ClientUiModel>> = MutableStateFlow(emptyList())
    val contacts: StateFlow<List<ClientUiModel>> = _contacts.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isFetchingClients = true) }

            getAllClientsUseCase.execute(Unit).onSuccess { clientsFlow ->

                clientsFlow.collectLatest {
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

    fun fetchContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val contactList = withContext(Dispatchers.IO) {
                val list = mutableListOf<ClientUiModel>()
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )

                cursor?.use {
                    val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

                    while (it.moveToNext()) {
                        val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                        val number = if (numberIndex != -1) it.getString(numberIndex).replace(" ", "").replace("-", "") else ""
                        val photo = if (photoIndex != -1) it.getString(photoIndex) else null

                        if (number.isNotEmpty() && list.none { c -> c.number == number }) {
                            list.add(
                                ClientUiModel(
                                    id = null,
                                    name = name,
                                    number = number,
                                    image = photo,
                                    address = null
                                )
                            )
                        }
                    }
                }
                list
            }
            _contacts.value = contactList
        }
    }

    fun syncAllContacts() {
        viewModelScope.launch {
            _contacts.value.forEach { contact ->
                syncContact(contact)
            }
        }
    }

    fun syncSelectedContacts(selectedContacts: List<ClientUiModel>) {
        viewModelScope.launch {
            selectedContacts.forEach { contact ->
                syncContact(contact)
            }
        }
    }

    private suspend fun syncContact(contact: ClientUiModel) {
        val phoneNumber = contact.number ?: return
        getClientByPhoneNumberUseCase.execute(phoneNumber).onSuccess { existingClient ->
            if (existingClient != null) {
                // Update name and photo if possible
                val updatedClient = existingClient.copy(
                    name = contact.name ?: existingClient.name,
                    avatarUri = contact.image ?: existingClient.avatarUri
                )
                updateClientUseCase.execute(updatedClient)
            } else {
                // Add
                addClientUseCase.execute(contact.toDomain())
            }
        }
    }
}
