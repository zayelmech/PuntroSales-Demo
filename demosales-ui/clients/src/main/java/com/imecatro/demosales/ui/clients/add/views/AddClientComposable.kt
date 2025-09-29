package com.imecatro.demosales.ui.clients.add.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.clients.R
import com.imecatro.demosales.ui.clients.add.model.AddClientUiModel
import com.imecatro.demosales.ui.clients.add.model.isFormValid
import com.imecatro.demosales.ui.clients.add.model.isLoading
import com.imecatro.demosales.ui.clients.add.model.isSaved
import com.imecatro.demosales.ui.clients.add.viewmodel.AddClientViewModel
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
internal fun AddClientComposable(
    isLoading: Boolean = false,
    uri: Uri? = null,
    onPickImage: () -> Unit = {},
    clientName: String = "",
    onClientNameChange: (String) -> Unit = {},
    phoneNumber: String = "",
    onPhoneNumberChange: (String) -> Unit = {},
    clientAddress: String = "",
    onClientAddressChange: (String) -> Unit = {},
    buttonSaveState: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSaveButtonClicked: () -> Unit = {}
) {

    val context = LocalContext.current

    LazyColumn {
        item {
            TopAppBar(title = { Text(text = stringResource(R.string.top_bar_client)) }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        null
                    )
                }
            })
            if (isLoading)
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = "Image", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.height(100.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(uri)
                                        .placeholder(R.drawable.baseline_mood_24)
                                        .error(R.drawable.baseline_mood_24)
                                        .crossfade(true)
                                        .build()
                                ),
                        contentDescription = null,
                        modifier = Modifier
                            .sizeIn(maxWidth = 100.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(25)),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        FilledTonalButton(onClick = { onPickImage() }) {
                            Icon(painterResource(R.drawable.gallery_images), null)
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.btn_pick_image))
                        }
                    }
                }
                //Client name
                Text(text = stringResource(R.string.client_name), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = clientName, onValueChange = onClientNameChange)
                Spacer(modifier = Modifier.height(10.dp))
                //Phone number
                Text(text = stringResource(R.string.txt_phone_number), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(10.dp))
                //Address
                Text(text = stringResource(R.string.txt_address), style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(
                    modifier = Modifier.padding(0.dp, 5.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                OutlinedTextField(
                    value = clientAddress,
                    onValueChange = onClientAddressChange,
                    singleLine = false,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(60.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = onSaveButtonClicked,
                        enabled = buttonSaveState,
                        modifier = Modifier
                            .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Filled.Done, null)
                        Text(text = stringResource(R.string.btn_save))
                    }
                }
            }
        }
    }

}


/**
 * Add client composable Implementation
 *
 * This composable is the implementation of the [AddClientComposable]
 *
 * It will holds the state of the form and will call the [AddClientViewModel.saveClient] in order to save the client
 *
 * @param addClientViewModel The [AddClientViewModel] that will be used to save the client
 * @param onClientSaved This lambda will be called when the client is saved
 */
@Composable
fun AddClientComposableImpl(
    addClientViewModel: AddClientViewModel,
    onBackToList: () -> Unit,
    onClientSaved: () -> Unit
) {


    val uiState by addClientViewModel.uiState.collectAsState()
    var formState by remember { mutableStateOf(AddClientUiModel()) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uriPicked: Uri? ->

        uriPicked?.let {
            context.saveMediaToStorage(it) { uri ->
                formState = formState.copy(imageUri = uri)
            }
        }
    }
    AddClientComposable(
        isLoading = uiState.isLoading,
        uri = formState.imageUri,
        onPickImage = { launcher.launch("image/*") },
        clientName = formState.clientName,
        onClientNameChange = { formState = formState.copy(clientName = it) },
        phoneNumber = formState.phoneNumber,
        onPhoneNumberChange = { formState = formState.copy(phoneNumber = it) },
        clientAddress = formState.clientAddress,
        onClientAddressChange = { formState = formState.copy(clientAddress = it) },
        buttonSaveState = (formState.isFormValid && !uiState.isLoading),
        onNavigateBack = onBackToList,
        onSaveButtonClicked = { addClientViewModel.saveClient(formState) }
    )

    // Observing viewmodel state
    LaunchedEffect(key1 = uiState) {
        if (uiState.isSaved) {
            onClientSaved()
        }
    }

    LaunchedEffect(key1 = formState) {
        addClientViewModel.onFormStateChangedAction(formState)
    }
}

