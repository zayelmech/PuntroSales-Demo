package com.imecatro.products.ui.catalog.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.common.SearchTopBar
import com.imecatro.products.ui.R
import com.imecatro.products.ui.catalog.state.ExportProductsState
import com.imecatro.products.ui.list.model.ProductUiModel

/**
 * Main state for the Catalog Summary Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishCatalogSummaryScreen(
    summary: ExportProductsState,
    onUpdate: (
        name: String,
        description: String,
        whatsapp: String,
        location: String,
        template: String,
        enableStock: Boolean
    ) -> Unit,
    onToggleProduct: (Long) -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showStoreDialog by remember { mutableStateOf(false) }
    var showProductDialog by remember { mutableStateOf(false) }
    var validationIssue by remember { mutableStateOf<SummaryValidationIssue?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.publish_catalog_summary_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomActions(
                onContinue = {
                    when {
                        summary.storeName.isBlank() || summary.whatsapp.isBlank() -> {
                            validationIssue = SummaryValidationIssue.StoreInformation
                        }

                        summary.selectedCount == 0 -> {
                            validationIssue = SummaryValidationIssue.Products
                        }

                        else -> onContinue()
                    }
                },
                onCancel = onCancel
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.publish_catalog_summary_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Store Info Card
            SummaryCard(
                title = summary.storeName,
                subtitle = summary.storeDescription.ifBlank { stringResource(R.string.publish_catalog_summary_no_description) },
                icon = Icons.Outlined.Store,
                buttonText = stringResource(R.string.publish_catalog_summary_btn_edit_info),
                onEditClick = { showStoreDialog = true }
            ) {
                InfoGrid(summary)
            }

            // Products Summary Card
            SummaryCard(
                title = stringResource(R.string.publish_catalog_summary_products_title),
                subtitle = pluralStringResource(
                    R.plurals.publish_catalog_summary_products_count,
                    summary.selectedCount,
                    summary.selectedCount
                ),
                icon = Icons.Outlined.Inventory2,
                buttonText = stringResource(R.string.publish_catalog_summary_btn_edit_products),
                onEditClick = { showProductDialog = true }
            ) {
                ProductStats(summary)
            }

            Text(
                text = stringResource(R.string.publish_catalog_summary_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
        }
    }

    if (showStoreDialog) {
        StoreInfoEditDialog(
            initialState = summary,
            onDismiss = { showStoreDialog = false },
            onSave = { name, description, whatsapp, location, template, enableStock ->
                onUpdate(name, description, whatsapp, location, template, enableStock)
                showStoreDialog = false
            }
        )
    }

    if (showProductDialog) {
        ProductSelectionDialog(
            products = summary.products.values.flatten(),
            selectedProductIds = summary.selectedProductIds,
            onDismiss = { showProductDialog = false },
            onToggleProduct = onToggleProduct
        )
    }

    validationIssue?.let { issue ->
        val isStoreInformationIssue = issue == SummaryValidationIssue.StoreInformation
        AlertDialog(
            onDismissRequest = { validationIssue = null },
            title = { Text(stringResource(R.string.publish_catalog_validation_title)) },
            text = {
                Text(
                    stringResource(
                        if (isStoreInformationIssue) {
                            R.string.publish_catalog_validation_store_info_message
                        } else {
                            R.string.publish_catalog_validation_products_message
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        validationIssue = null
                        if (isStoreInformationIssue) {
                            showStoreDialog = true
                        } else {
                            showProductDialog = true
                        }
                    }
                ) {
                    Text(
                        stringResource(
                            if (isStoreInformationIssue) {
                                R.string.publish_catalog_validation_edit_store
                            } else {
                                R.string.publish_catalog_validation_edit_products
                            }
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { validationIssue = null }) {
                    Text(stringResource(R.string.publish_catalog_validation_dismiss))
                }
            }
        )
    }
}

private enum class SummaryValidationIssue {
    StoreInformation,
    Products
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
private fun InfoGrid(state: ExportProductsState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoRow(Icons.Outlined.Payments, stringResource(R.string.publish_catalog_summary_label_currency), state.currency)
        InfoRow(Icons.AutoMirrored.Outlined.Chat, stringResource(R.string.publish_catalog_summary_label_whatsapp), state.whatsapp)
        InfoRow(Icons.Outlined.Place, stringResource(R.string.publish_catalog_summary_label_location), state.location.ifBlank { stringResource(R.string.publish_catalog_summary_location_empty) })
        InfoRow(Icons.Outlined.Palette, stringResource(R.string.publish_catalog_summary_label_template), state.template)
        InfoRow(
            Icons.Outlined.Inventory,
            stringResource(R.string.publish_catalog_summary_label_stock_visible),
            if (state.enableStock) stringResource(R.string.publish_catalog_summary_stock_enabled) else stringResource(R.string.publish_catalog_summary_stock_disabled)
        )
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

@Composable
private fun ProductStats(state: ExportProductsState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatItem(stringResource(R.string.publish_catalog_summary_stat_categories), state.categoriesCount.toString(), Icons.Outlined.Category)
            StatItem(stringResource(R.string.publish_catalog_summary_stat_with_image), state.withImageCount.toString(), Icons.Outlined.Image)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatItem(stringResource(R.string.publish_catalog_summary_stat_without_image), state.withoutImageCount.toString(), Icons.Outlined.HideImage)
            StatItem(stringResource(R.string.publish_catalog_summary_stat_unavailable), state.unavailableCount.toString(), Icons.Outlined.Block, MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector, valueColor: Color = Color.Unspecified) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(140.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreInfoEditDialog(
    initialState: ExportProductsState,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        description: String,
        whatsapp: String,
        location: String,
        template: String,
        enableStock: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialState.storeName) }
    var description by remember { mutableStateOf(initialState.storeDescription) }
    var whatsapp by remember { mutableStateOf(initialState.whatsapp) }
    val currency by remember { mutableStateOf(initialState.currency) }
    var location by remember { mutableStateOf(initialState.location) }
    var template by remember { mutableStateOf(initialState.template) }
    var enableStock by remember { mutableStateOf(initialState.enableStock) }
    var showValidationErrors by remember { mutableStateOf(false) }

    var templateExpanded by remember { mutableStateOf(false) }
    val templates = listOf("store", "restaurant-menu")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.publish_catalog_edit_title), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                val hasRequiredInformation =
                                    name.isNotBlank() && whatsapp.isNotBlank()
                                showValidationErrors = !hasRequiredInformation
                                if (hasRequiredInformation) {
                                    onSave(
                                        name.trim(),
                                        description.trim(),
                                        whatsapp.trim(),
                                        location.trim(),
                                        template,
                                        enableStock
                                    )
                                }
                            }
                        ) {
                            Text(stringResource(R.string.btn_save), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
//                // Logo Placeholder
//                Box(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .align(Alignment.CenterHorizontally)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.surfaceVariant)
//                        .clickable { /* Handle image pick */ },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Outlined.AddAPhoto,
//                        contentDescription = null,
//                        modifier = Modifier.size(32.dp),
//                        tint = MaterialTheme.colorScheme.outline
//                    )
//                }
//                Text(
//                    stringResource(R.string.publish_catalog_edit_logo_placeholder),
//                    style = MaterialTheme.typography.labelMedium,
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    color = MaterialTheme.colorScheme.outline
//                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.publish_catalog_edit_label_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = showValidationErrors && name.isBlank(),
                    supportingText = if (showValidationErrors && name.isBlank()) {
                        { Text(stringResource(R.string.publish_catalog_edit_required_error)) }
                    } else {
                        null
                    }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.publish_catalog_edit_label_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    label = { Text(stringResource(R.string.publish_catalog_edit_label_whatsapp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, null) },
                    isError = showValidationErrors && whatsapp.isBlank(),
                    supportingText = if (showValidationErrors && whatsapp.isBlank()) {
                        { Text(stringResource(R.string.publish_catalog_edit_required_error)) }
                    } else {
                        null
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { /* Read only */ },
                        label = { Text(stringResource(R.string.publish_catalog_edit_label_currency)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        enabled = false
                    )

                    ExposedDropdownMenuBox(
                        expanded = templateExpanded,
                        onExpandedChange = { templateExpanded = !templateExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = template,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.publish_catalog_edit_label_template)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateExpanded) },
                            modifier = Modifier.menuAnchor(
                                ExposedDropdownMenuAnchorType.PrimaryNotEditable
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = templateExpanded,
                            onDismissRequest = { templateExpanded = false }
                        ) {
                            templates.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        template = option
                                        templateExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(stringResource(R.string.publish_catalog_edit_label_location)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Place, null) }
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.publish_catalog_edit_label_show_stock)) },
                    supportingContent = { Text(stringResource(R.string.publish_catalog_edit_desc_show_stock)) },
                    trailingContent = {
                        Switch(
                            checked = enableStock,
                            onCheckedChange = { enableStock = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSelectionDialog(
    products: List<ProductUiModel>,
    selectedProductIds: Set<Long>,
    onDismiss: () -> Unit,
    onToggleProduct: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                SearchTopBar(
                    title = stringResource(R.string.publish_catalog_selection_title),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClearSearchBar = { searchQuery = "" },
                    extraActions = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(tonalElevation = 8.dp) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.btn_save), fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
//                // Filters Row
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .horizontalScroll(rememberScrollState())
//                        .padding(horizontal = 16.dp, vertical = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    FilterChip(selected = true, onClick = {}, label = { Text(stringResource(R.string.publish_catalog_selection_filter_all)) })
//                    FilterChip(selected = false, onClick = {}, label = { Text(stringResource(R.string.publish_catalog_selection_filter_selected)) })
//                    FilterChip(selected = false, onClick = {}, label = { Text(stringResource(R.string.publish_catalog_selection_filter_no_image)) })
//                    FilterChip(selected = false, onClick = {}, label = { Text(stringResource(R.string.publish_catalog_selection_filter_categories)) })
//                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }) { product ->
                        ProductItem(
                            product = product,
                            isSelected = product.id in selectedProductIds,
                            onToggle = {
                                product.id?.let { onToggleProduct(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: ProductUiModel,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .error(R.drawable.baseline_insert_photo_24)
                            .placeholder(R.drawable.baseline_insert_photo_24)
                            .crossfade(true)
                            .build()

                    ),null)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name ?: stringResource(R.string.publish_catalog_selection_no_name), fontWeight = FontWeight.Bold)
                Text("${product.category ?: stringResource(R.string.publish_catalog_selection_no_category)} • ${product.price}/${product.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun BottomActions(
    onContinue: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.publish_catalog_preview_btn_cancel), color = MaterialTheme.colorScheme.outline)
            }

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.publish_catalog_btn_continue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PublishCatalogSummaryScreenPreview() {
    PuntroSalesDemoTheme {
        PublishCatalogSummaryScreen(
            summary = ExportProductsState(
                storeName = "Mi Tiendita Premium",
                storeDescription = "Los mejores productos artesanales a tu alcance.",
                whatsapp = "5512345678",
                currency = "MXN",
                location = "Ciudad de México"
            ),
            onUpdate = { _, _, _, _, _, _ -> },
            onToggleProduct = {},
            onContinue = {},
            onCancel = {  },

        )
    }
}
@Preview(showBackground = true)
@Composable
private fun StoreInfoEditDialogPreview() {
    PuntroSalesDemoTheme {
        StoreInfoEditDialog(
            initialState = ExportProductsState(
                storeName = "Mi Tiendita Premium",
                storeDescription = "Los mejores productos artesanales a tu alcance.",
                whatsapp = "5512345678",
                currency = "MXN",
                location = "Ciudad de México"
            ),
            onDismiss = {},
            onSave = { _, _, _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductSelectionDialogPreview() {
    PuntroSalesDemoTheme {
        ProductSelectionDialog(
            products = listOf(
                ProductUiModel(1L, "Café Gourmet", "150", "kg", "10", null, "Bebidas"),
                ProductUiModel(2L, "Mermelada Fresa", "85", "pza", "5", null, "Dulces"),
                ProductUiModel(3L, "Pan Artesanal", "40", "pza", "20", null, "Panadería")
            ),
            selectedProductIds = setOf(1L, 3L),
            onDismiss = {},
            onToggleProduct = {}
        )
    }
}
