package com.imecatro.demosales.profile.ui.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.R
import com.imecatro.demosales.domain.core.model.Currencies
import com.imecatro.demosales.domain.core.model.Languages
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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showStoreDialog by remember { mutableStateOf(false) }

    val onImagePicked: (Uri?) -> Unit = { uri ->
        uri?.let {
            context.saveMediaToStorage(it) { localUri ->
                viewModel.onUpdateLogo(localUri.toString())
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImagePicked
    )

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onImagePicked
    )

    ProfileSettings(
        profile = uiState.profile,
        onUpdateInfo = viewModel::onUpdateInfo,
        onLanguageSelected = viewModel::onUpdateLanguage,
        onDarkThemeEnabled = viewModel::onUpdateTheme,
        openImagePicker = {
            try {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } catch (_: Exception) {
                getContentLauncher.launch("image/*")
            }
        },
        showStoreDialog = showStoreDialog,
        onDismissDialog = { showStoreDialog = false },
        onShowDialog = { showStoreDialog = true }
    )

    UiStateHandler(uiState, onDismiss = viewModel::onErrorMessage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
    profile: UserProfileUiModel,
    onUpdateInfo: (String, String, String, String, String) -> Unit = { _, _, _, _, _-> },
    onLanguageSelected: (String) -> Unit = {},
    onDarkThemeEnabled: (Boolean) -> Unit = {},
    openImagePicker: () -> Unit = {},
    showStoreDialog: Boolean = false,
    onDismissDialog: () -> Unit = {},
    onShowDialog: () -> Unit = {}
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_profile)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo / Cover Image
            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))) {
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
                        .requiredSizeIn(maxHeight = 200.dp)
                        .fillMaxWidth()
                        .clickable { openImagePicker() },
                    contentScale = ContentScale.Crop
                )
            }

            // Store Info Card
            SummaryCard(
                title = profile.storeName,
                subtitle = profile.storeDescription.ifBlank { stringResource(ProductR.string.publish_catalog_summary_no_description) },
                icon = Icons.Outlined.Store,
                buttonText = stringResource(ProductR.string.publish_catalog_summary_btn_edit_info),
                onEditClick = onShowDialog
            ) {
                InfoGrid(profile)
            }

            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.txt_language)) },
                trailingContent = {
                    DropListPicker(
                        list = Languages.entries.map { it.displayName },
                        itemSelected = profile.language,
                        onItemClicked = onLanguageSelected
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

    if (showStoreDialog) {
        StoreInfoEditDialog(
            profile = profile,
            onDismiss = onDismissDialog,
            onSave = onUpdateInfo
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    buttonText: String,
    onEditClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            content()

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(buttonText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun InfoGrid(profile: UserProfileUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoRow(Icons.Outlined.Payments, stringResource(ProductR.string.publish_catalog_summary_label_currency), profile.currency)
        InfoRow(Icons.AutoMirrored.Outlined.Chat, stringResource(ProductR.string.publish_catalog_summary_label_whatsapp), profile.whatsapp.ifBlank { "Not set" })
        InfoRow(Icons.Outlined.Place, stringResource(ProductR.string.publish_catalog_summary_label_location), profile.location.ifBlank { stringResource(ProductR.string.publish_catalog_summary_location_empty) })
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreInfoEditDialog(
    profile: UserProfileUiModel,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(profile.storeName) }
    var description by remember { mutableStateOf(profile.storeDescription) }
    var whatsapp by remember { mutableStateOf(profile.whatsapp) }
    var currency by remember { mutableStateOf(profile.currency) }
    var location by remember { mutableStateOf(profile.location) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(ProductR.string.publish_catalog_edit_title), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                onSave(name, description, whatsapp, location, currency)
                                onDismiss()
                            }
                        ) {
                            Text(stringResource(ProductR.string.btn_save), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(ProductR.string.publish_catalog_edit_label_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(ProductR.string.publish_catalog_edit_label_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    label = { Text(stringResource(ProductR.string.publish_catalog_edit_label_whatsapp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, null) }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DropListPicker(
                            list = Currencies.entries.map { it.code },
                            itemSelected = currency,
                            onItemClicked = { currency = it }
                        )
                    }

                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(stringResource(ProductR.string.publish_catalog_edit_label_location)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Place, null) }
                )
            }
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
                storeDescription = "The best demo store in the world.",
                storeLogoUri = "",
                whatsapp = "1234567890",
                location = "Online",
                language = "English",
                currency = "USD",
                isDarkTheme = false,
            )
        )
    }
}
