package com.imecatro.demosales.ui.clients.list.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel
import com.imecatro.demosales.ui.clients.list.model.imageUrl

@Composable
fun SyncOptionsDialog(
    onDismissRequest: () -> Unit,
    onSyncAll: () -> Unit,
    onSelectSpecific: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.tittle_sync_contacts)) },
        text = { Text(stringResource(R.string.txt_select_sync_option)) },
        confirmButton = {
            TextButton(onClick = {
                onSyncAll()
                onDismissRequest()
            }) {
                Text(stringResource(R.string.btn_sync_all_contacts))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onSelectSpecific()
                onDismissRequest()
            }) {
                Text(stringResource(R.string.btn_sync_specific_contact))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSelectionDialog(
    contacts: List<ClientUiModel>,
    onDismissRequest: () -> Unit,
    onContactsSelected: (List<ClientUiModel>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateListOf<ClientUiModel>() }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isEmpty()) {
            contacts
        } else {
            contacts.filter {
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                        it.number?.contains(searchQuery) == true
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Select Contacts") },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                onContactsSelected(selectedContacts.toList())
                                onDismissRequest()
                            },
                            enabled = selectedContacts.isNotEmpty(),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Add (${selectedContacts.size})")
                        }
                    }
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text(stringResource(R.string.label_search_contact)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredContacts) { contact ->
                        ContactListItem(
                            contact = contact,
                            isSelected = selectedContacts.contains(contact),
                            onToggleSelection = {
                                if (selectedContacts.contains(contact)) {
                                    selectedContacts.remove(contact)
                                } else {
                                    selectedContacts.add(contact)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactListItem(
    contact: ClientUiModel,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    ListItem(
        headlineContent = { Text(contact.name ?: "") },
        supportingContent = { Text(contact.number ?: "") },
        leadingContent = {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(contact.imageUrl)
                        .error(R.drawable.baseline_mood_24)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelection() })
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}
