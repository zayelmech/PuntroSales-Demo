package com.imecatro.demosales.profile.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.R
import com.imecatro.demosales.profile.ui.mappers.toUiModel
import com.imecatro.demosales.profile.ui.model.UserProfileUiModel
import com.imecatro.demosales.profile.ui.viewmodels.ProfileViewModel
import com.imecatro.demosales.ui.theme.DropListPicker
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.products.ui.R as ProductR

@Composable
fun ProfileSettingsStateImpl(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                context.saveMediaToStorage(it) { localUri ->
                    viewModel.onUpdateLogo(localUri.toString())
                }
            }
        }
    )

    ProfileSettings(
        profile = uiState.profile.toUiModel(),
        onBack = onBack,
        onStoreNameChange = viewModel::onUpdateStoreName,
        onLanguageSelected = viewModel::onUpdateLanguage,
        onCurrencySelected = viewModel::onUpdateCurrency,
        onDarkThemeEnabled = viewModel::onUpdateTheme,
        openImagePicker = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        })

    LaunchedEffect(uiState.profile) {
        viewModel.onSaveSettings()
    }
    UiStateHandler(uiState, onDismiss = viewModel::onErrorMessage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
    profile: UserProfileUiModel,
    onBack: () -> Unit = {},
    onStoreNameChange: (String) -> Unit = {},
    onLanguageSelected: (String) -> Unit = {},
    onCurrencySelected: (String) -> Unit = {},
    onDarkThemeEnabled: (Boolean) -> Unit = {},
    openImagePicker: () -> Unit = {},
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember(profile.storeName) { mutableStateOf(profile.storeName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_profile)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = if (view.isInEditMode) {
                        painterResource(id = ProductR.drawable.baseline_insert_photo_24)
                    } else {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(profile.storeLogoUri.ifEmpty { ProductR.drawable.baseline_insert_photo_24 })
                                .error(ProductR.drawable.baseline_insert_photo_24)
                                .crossfade(true)
                                .build()
                        )
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .requiredSizeIn(maxHeight = 240.dp)
                        .fillMaxWidth()
                        .clickable { openImagePicker() },
                    contentScale = ContentScale.Crop
                )
            }

            ListItem(
                headlineContent = {
                    if (isEditingName) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.txt_store_name)) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    onStoreNameChange(tempName)
                                    isEditingName = false
                                }) {
                                    Icon(Icons.Default.Check, null)
                                }
                            }
                        )
                    } else {
                        Text(profile.storeName, style = MaterialTheme.typography.titleLarge)
                    }
                },
                trailingContent = {
                    if (!isEditingName) {
                        IconButton(onClick = { isEditingName = true }) {
                            Icon(Icons.Default.Edit, null)
                        }
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                headlineContent = { Text(stringResource(R.string.txt_language)) },
                trailingContent = {
                    DropListPicker(
                        list = listOf("English", "Spanish"),
                        itemSelected = profile.language,
                        onItemClicked = onLanguageSelected
                    )
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.txt_currency)) },
                trailingContent = {
                    DropListPicker(
                        list = listOf("USD", "MXN", "EUR"),
                        itemSelected = profile.currency,
                        onItemClicked = onCurrencySelected
                    )
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.txt_dark_theme)) },
                trailingContent = {
                    Switch(
                        checked = profile.isDarkTheme,
                        onCheckedChange = onDarkThemeEnabled
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileSettings() {
    PuntroSalesDemoTheme {
        ProfileSettings(
            profile = UserProfileUiModel(
                storeName = "Puntro Sales Demo",
                storeLogoUri = "",
                language = "English",
                currency = "USD",
                isDarkTheme = false
            )
        )
    }
}
