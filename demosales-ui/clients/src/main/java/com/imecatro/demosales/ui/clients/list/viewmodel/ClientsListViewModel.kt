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
import com.imecatro.demosales.domain.clients.usecases.DeleteClientByIdUseCase
import com.imecatro.demosales.domain.clients.usecases.SearchClientUseCase
import com.imecatro.demosales.ui.theme.architect.BaseViewModel
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
    private val getClientByPhoneNumberUseCase: GetClientByPhoneNumberUseCase,
    private val searchClientUseCase: SearchClientUseCase,
    private val deleteClientByIdUseCase: DeleteClientByIdUseCase
) : BaseViewModel<ClientsListPresenterModel>(ClientsListPresenterModel()) {

    private val _contacts: MutableStateFlow<List<ClientUiModel>> = MutableStateFlow(emptyList())
    val contacts: StateFlow<List<ClientUiModel>> = _contacts.asStateFlow()

    override fun onStart() {
        viewModelScope.launch {
            updateState { copy(isFetchingClients = true) }

            getAllClientsUseCase.execute(Unit).onSuccess { clientsFlow ->
                clientsFlow.collectLatest { clients ->
                    updateState {
                        copy(
                            clients = clients.toUiModel().map { client ->
                                client.copy(isSelected = idsSelected.contains(client.id))
                            },
                            isFetchingClients = false
                        )
                    }
                }
            }.onFailure {
                updateState {
                    copy(
                        errors = it.message,
                        isFetchingClients = false
                    )
                }
            }
        }
    }

    fun onSearchAction(query: String) {
        viewModelScope.launch {
            searchClientUseCase(query).collectLatest { list ->
                updateState { copy(clientsFiltered = list.toUiModel()) }
            }
        }
    }

    fun onClientSelected(id: Long?) {
        if (id == null) {
            updateState { copy(enableSelection = true) }
            return
        }
        val currentIds = uiState.value.idsSelected
        val newIds = if (currentIds.contains(id)) {
            currentIds.minus(id)
        } else {
            currentIds.plus(id)
        }
        
        updateState { 
            copy(
                idsSelected = newIds,
                clients = clients.map { it.copy(isSelected = newIds.contains(it.id)) }
            ) 
        }

        if (uiState.value.clients.size != newIds.size) {
            updateState { copy(allSelected = false) }
        } else if (newIds.isNotEmpty()) {
            updateState { copy(allSelected = true) }
        }
    }

    fun onSelectAll(checked: Boolean) {
        updateState {
            val allIds = if (checked) clients.mapNotNull { it.id } else emptyList()
            copy(
                allSelected = checked,
                idsSelected = allIds,
                clients = clients.map { it.copy(isSelected = checked) }
            )
        }
    }

    fun onClearSelections() {
        updateState {
            copy(
                idsSelected = emptyList(),
                enableSelection = false,
                allSelected = false,
                clients = clients.map { it.copy(isSelected = false) }
            )
        }
    }

    fun onDeleteSelectedClients() {
        viewModelScope.launch {
            uiState.value.idsSelected.forEach { id ->
                deleteClientByIdUseCase.execute(id)
            }
            onClearSelections()
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
