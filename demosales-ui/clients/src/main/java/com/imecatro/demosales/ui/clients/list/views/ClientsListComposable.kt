package com.imecatro.demosales.ui.clients.list.views

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.list.model.ClientUiModel
import com.imecatro.demosales.ui.clients.list.model.ClientsListPresenterModel
import com.imecatro.demosales.ui.clients.list.model.imageUrl
import com.imecatro.demosales.ui.clients.list.viewmodel.ClientsListViewModel
import com.imecatro.demosales.ui.theme.common.SearchTopBar
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import kotlinx.coroutines.flow.debounce


private val ClientsListPresenterModel.isLoading: Boolean
    get() {
        return isFetchingClients
    }


@Preview(showBackground = true)
@Composable
internal fun ClientCardCompose(
    client: ClientUiModel = ClientUiModel.getDummy(),
    onLongClicked: () -> Unit = {},
    onCardClicked: () -> Unit = {}
) {
    val view = LocalView.current
    val backgroundColor = if (client.isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = onCardClicked,
                onLongClick = onLongClicked
            ),
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            headlineContent = {
                Text(text = client.name ?: "")
            },
            leadingContent = {
                Box(contentAlignment = Alignment.TopEnd) {
                    Image(
                        painter = if (view.isInEditMode)
                            painterResource(id = R.drawable.baseline_mood_24)
                        else rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(client.imageUrl)
                                .error(R.drawable.baseline_mood_24)
                                .crossfade(true)
                                .build()

                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(25)),
                        contentScale = ContentScale.FillWidth
                    )
                    if (client.isSelected)
                        Icon(
                            Icons.Default.CheckCircle,
                            "Selected",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                }
            },
            supportingContent = {
                Text(text = client.address ?: "")
            },
            overlineContent = {
                Text(text = client.number ?: "")
            },
            colors = ListItemDefaults.colors(containerColor = backgroundColor)

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ListOfClients(
    list: List<ClientUiModel> = emptyList(),
    isLoading: Boolean = false,
    onSearchClient: (String) -> Unit = {},
    searchList: List<ClientUiModel> = emptyList(),
    onClientSelected: (Long?) -> Unit = {},
    itemsSelectedQty: Int = 0,
    showOptions: Boolean = false,
    onHideOptions: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onSelectAllChecked: (Boolean) -> Unit = {},
    allSelected: Boolean = false,
    onCardClicked: (Long?) -> Unit = {},
    onNavigateAction: () -> Unit = {},
    onSyncClicked: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        snapshotFlow { text }
            .debounce(300)
            .collect {
                onSearchClient(it)
            }
    }

    Scaffold(
        topBar = {
            if (isLoading)
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            if (showOptions) {
                TopAppBar(
                    title = { Text("$itemsSelectedQty") },
                    navigationIcon = {
                        IconButton(onClick = onHideOptions) {
                            Icon(Icons.Default.Close, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = onDeleteClicked) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected")
                        }
                        Checkbox(allSelected, onCheckedChange = { onSelectAllChecked(it) })
                    }
                )
            } else {
                val windowInsets = TopAppBarDefaults.windowInsets
                Column(modifier = Modifier.windowInsetsPadding(windowInsets)) {
                    SearchTopBar(
                        title = stringResource(R.string.top_bar_clients),
                        query = text,
                        onQueryChange = { text = it },
                        onSearchAction = { onSearchClient(text) },
                        onClearSearchBar = { text = "" },
                        extraActions = {
                            IconButton(onClick = onSyncClicked) {
                                Icon(Icons.Default.Contacts, contentDescription = "Sync Contacts")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onNavigateAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_client),
                    contentDescription = null
                )

            }
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = paddingValues
        ) {
            items(if (text.isEmpty()) list else searchList) { client ->
                ClientCardCompose(
                    client = client,
                    onLongClicked = { if (text.isEmpty()) onClientSelected(client.id) },
                    onCardClicked = { onCardClicked(client.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ClientListImpl(
    clientsListViewModel: ClientsListViewModel,
    onNavigateAction: (Long?) -> Unit
) {
    val uiState by clientsListViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val contacts by clientsListViewModel.contacts.collectAsState()

    var showSyncOptions by remember { mutableStateOf(false) }
    var showContactSelection by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            clientsListViewModel.fetchContacts(context.contentResolver)
            showSyncOptions = true
        }
    }

    val showOptions = uiState.idsSelected.isNotEmpty() || uiState.enableSelection

    ListOfClients(
        list = uiState.clients,
        isLoading = uiState.isLoading,
        onSearchClient = { clientsListViewModel.onSearchAction(it) },
        searchList = uiState.clientsFiltered,
        onClientSelected = { clientsListViewModel.onClientSelected(it) },
        itemsSelectedQty = uiState.idsSelected.size,
        showOptions = showOptions,
        onHideOptions = { clientsListViewModel.onClearSelections() },
        onDeleteClicked = { showDeleteDialog = true },
        onSelectAllChecked = { clientsListViewModel.onSelectAll(it) },
        allSelected = uiState.allSelected,
        onCardClicked = { id ->
            if (showOptions) {
                clientsListViewModel.onClientSelected(id)
            } else {
                onNavigateAction(id)
            }
        },
        onNavigateAction = { onNavigateAction(null) },
        onSyncClicked = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) -> {
                    clientsListViewModel.fetchContacts(context.contentResolver)
                    showSyncOptions = true
                }

                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        }
    )

    if (showSyncOptions) {
        SyncOptionsDialog(
            onDismissRequest = { showSyncOptions = false },
            onSyncAll = { clientsListViewModel.syncAllContacts() },
            onSelectSpecific = { showContactSelection = true }
        )
    }

    if (showContactSelection) {
        ContactSelectionDialog(
            contacts = contacts,
            onDismissRequest = { showContactSelection = false },
            onContactsSelected = { selected ->
                clientsListViewModel.syncSelectedContacts(selected)
            }
        )
    }

    if (showDeleteDialog)
        ActionDialog(
            dialogType = DialogType.Delete,
            message = stringResource(R.string.delete_client_message),
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirmClicked = {
                showDeleteDialog = false
                clientsListViewModel.onDeleteSelectedClients()
            })
}
