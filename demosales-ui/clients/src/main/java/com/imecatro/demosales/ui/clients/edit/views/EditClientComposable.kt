package com.imecatro.demosales.ui.clients.edit.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.imecatro.demosales.ui.clients.add.views.AddClientComposable
import com.imecatro.demosales.ui.clients.edit.model.isFormValid
import com.imecatro.demosales.ui.clients.edit.model.isLoading
import com.imecatro.demosales.ui.clients.edit.viewmodel.EditClientViewModel
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage


@Composable
fun EditClientComposableImpl(
    viewModel: EditClientViewModel,
    onBackClicked: () -> Unit = {},
    onClientSaved: () -> Unit = {},
) {

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uriPicked: Uri? ->

        uriPicked?.let {
            context.saveMediaToStorage(it) { uri ->
                viewModel.onImageUriChangeAction(uri)
            }
        }
    }

    AddClientComposable(
        isLoading = uiState.isLoading,
        uri = uiState.imageUri,
        onPickImage = { launcher.launch("image/*") },
        clientName = uiState.clientName,
        onClientNameChange = (viewModel::onClientNameChange),
        phoneNumber = uiState.phoneNumber,
        onPhoneNumberChange = (viewModel::onPhoneNumberChange),
        clientAddress = uiState.clientAddress,
        onClientAddressChange = (viewModel::onClientAddressChange),
        buttonSaveState = uiState.isFormValid && !uiState.isLoading,
        onNavigateBack = onBackClicked,
        onSaveButtonClicked = { viewModel.onSaveClientAction(uiState) }
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.isClientEdited) {
            onClientSaved()
        }
    }
}