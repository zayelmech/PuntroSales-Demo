package com.imecatro.products.ui.add.views

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.DropListPicker
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.barcode.ScanBarcodeActivity
import com.imecatro.demosales.ui.theme.common.CurrencyVisualTransformation
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.createImageFile
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.demosales.ui.theme.dialogs.InputTextDialogComposable
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.viewmodel.AddViewModel
import kotlinx.coroutines.launch
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddProductAdvancedOptionsPreview() {
    AddProductComposable(
        productName = "",
        productPrice = "10.00",
        stock = "5",
        buttonSaveState = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Advanced Options Expanded")
@Composable
fun AddProductAdvancedOptionsExpandedPreview() {
    AddProductComposable(
        productName = "Sample Product",
        productPrice = "10.00",
        stock = "5",
        buttonSaveState = true,
        initialShowAdvanced = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductComposable(
    uri: Uri? = null,
    onPickImage: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    productName: String = "",
    onProductNameChange: (String) -> Unit = {},
    productPrice: String = "",
    onProductPriceChange: (String) -> Unit = {},
    //currencyList: List<String> = emptyList(),
    //currencyPicked: String = "",
    //onCurrencyChange: (String) -> Unit = {},
    unitList: List<String> = emptyList(),
    unitPicked: String = "",
    onUnitPicked: (String) -> Unit = {},
    categories: List<String> = emptyList(),
    categoryPicked: String = "",
    onCategoryPicked: (String) -> Unit = {},
    onAddNewCategory: () -> Unit = {},
    stock: String = "",
    onStockChange: (String) -> Unit = {},
    onEditStock: () -> Unit = {},
    isEditMode: Boolean = false,
    onBarcodeClicked: () -> Unit = {},
    barcode: String = "",
    onBarcodeChange: (String) -> Unit = {},
    detailsText: String = " -- ",
    onDetailsChange: (String) -> Unit = {},
    buttonSaveState: Boolean = false,
    onBackToList: () -> Unit = {},
    onSaveButtonClicked: () -> Unit = {},
    initialShowAdvanced: Boolean = false
) {

    val context = LocalContext.current
    val currencyPicked = LocalCurrencyCode.current

    val state = rememberTooltipState()
    val scope = rememberCoroutineScope()

    var showAdvanced by remember { mutableStateOf(initialShowAdvanced) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (!isEditMode) stringResource(R.string.top_bar_title_add) else stringResource(
                            R.string.top_bar_title_edit
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackToList() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Button(
                        enabled = buttonSaveState,
                        onClick = onSaveButtonClicked,
                        modifier = Modifier
                            .padding(16.dp)
                            .sizeIn(minHeight = 50.dp)
                            .widthIn(max = 400.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Filled.Done, null)
                        Spacer(Modifier.size(8.dp))
                        Text(text = stringResource(R.string.btn_save))
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = productName,
                        label = { Text(text = stringResource(R.string.label_product_name)) },
                        supportingText = {
                            if (productName.isBlank()) Text(
                                stringResource(R.string.supporting_name_txt),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        onValueChange = onProductNameChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = onProductPriceChange,
                        label = { Text(text = stringResource(R.string.label_product_price)) },
                        placeholder = { Text("0.0".formatAsCurrency()) },
                        supportingText = {
                            if (productPrice.isBlank()) Text(
                                stringResource(R.string.supporting_price_txt),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = CurrencyVisualTransformation(currencyCode = currencyPicked),
                        suffix = { Text(currencyPicked) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            enabled = !isEditMode,
                            value = stock,
                            label = { Text(text = stringResource(R.string.label_stock)) },
                            placeholder = { Text("0.0") },
                            supportingText = {
                                if (stock.isBlank()) Text(
                                    stringResource(R.string.supporting_stock_txt),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            onValueChange = onStockChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip { Text(stringResource(R.string.tooltip_info_stock)) }
                                    },
                                    state = state
                                ) {
                                    IconButton(
                                        onClick = { scope.launch { state.show() } },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Info"
                                        )
                                    }
                                }
                            }
                        )
                        if (isEditMode) {
                            Spacer(modifier = Modifier.width(16.dp))
                            FilledTonalIconButton(onClick = { onEditStock() }) {
                                Icon(Icons.Default.Edit, "Edit Stock")
                            }
                        }
                    }
                }

                item {
                    TextButton(
                        onClick = { showAdvanced = !showAdvanced },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (showAdvanced) stringResource(R.string.btn_hide_advanced_options)
                            else stringResource(R.string.btn_advanced_options)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(
                            imageVector = if (showAdvanced) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }

                if (showAdvanced) {
                    item {
                        Text(
                            text = stringResource(R.string.txt_image),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Row(
                            Modifier
                                .height(120.dp)
                                .padding(vertical = 8.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(uri)
                                        .placeholder(R.drawable.baseline_add_photo_alternate_24)
                                        .error(R.drawable.baseline_add_photo_alternate_24)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                FilledTonalButton(
                                    onClick = { onPickImage() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(painterResource(R.drawable.gallery_images), null)
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(stringResource(R.string.btn_image_picker))
                                }

                                FilledTonalButton(
                                    onClick = { onTakePhoto() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(painterResource(R.drawable.camera), null)
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(stringResource(R.string.btn_take_photo))
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = barcode,
                            placeholder = { Text("000000000000000") },
                            label = { Text(text = stringResource(R.string.label_barcode)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = onBarcodeChange,
                            trailingIcon = {
                                IconButton(onClick = onBarcodeClicked) {
                                    Icon(painterResource(R.drawable.barcode), null)
                                }
                            },
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.label_unit),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                DropListPicker(
                                    unitList, unitPicked
                                ) { unitPicked ->
                                    onUnitPicked(unitPicked)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.label_category),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                DropListPicker(
                                    categories, categoryPicked, onAddItem = onAddNewCategory
                                ) { category ->
                                    onCategoryPicked(category)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.label_details),
                            style = MaterialTheme.typography.labelLarge
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp
                        )

                        OutlinedTextField(
                            value = detailsText,
                            onValueChange = onDetailsChange,
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

}


@Composable
fun AddProductComposableStateImpl(
    addViewModel: AddViewModel,
    onBackToList: () -> Unit,
    onSaveAction: () -> Unit
) {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Image capture was successful, do something with the URI
                imageUri?.let {
                    context.saveMediaToStorage(it) { uri ->
                        imageUri = uri
                    }
                }

            }
        }
    val requestCameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, launch camera
                imageUri = context.createImageFile()
                cameraLauncher.launch(imageUri!!)
            } else {
                // Permission denied
                // Handle permission denial (e.g., show a message to the user)
            }
        }

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uriPicked: Uri? ->

        uriPicked?.let {
            context.saveMediaToStorage(it) { uri ->
                imageUri = uri
            }
        }
    }


    var productName by remember {
        mutableStateOf("")
    }
    var productPrice by remember {
        mutableStateOf("")
    }

    val locale: Locale = Locale.getDefault()
    var currencySelected by remember {
        mutableStateOf(Money.getCurrency(locale))
    }
    var unitSelected by remember {
        mutableStateOf("pz")
    }

    var showAddNewCategory by remember {
        mutableStateOf(false)
    }

    var barcode by remember {
        mutableStateOf("")
    }

    var details by remember {
        mutableStateOf("")
    }

    var stock by remember {
        mutableStateOf("")
    }

    var buttonEnableState by remember {
        mutableStateOf(false)
    }

    if (productName.isNotEmpty() && productPrice.isNotEmpty() && stock.isNotBlank()) {
        buttonEnableState = true
    }

    val barcodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedBarcode = result.data?.getStringExtra("barcode_result")
            if (scannedBarcode != null) {
                barcode = scannedBarcode
            }
        }
    }

    val uiState by addViewModel.uiState.collectAsState()

    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        onTakePhoto = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        productName = productName,
        onProductNameChange = { productName = it },
        productPrice = productPrice,
        onProductPriceChange = { productPrice = it },
        unitList = addViewModel.getUnities(),
        unitPicked = unitSelected,
        onUnitPicked = { unitSelected = it },
        categories = uiState.categoriesNames,
        categoryPicked = uiState.category,
        onCategoryPicked = { addViewModel.onCategoryPicked(it) },
        onAddNewCategory = { showAddNewCategory = true },
        barcode = barcode,
        onBarcodeChange = { barcode = it },
        onBarcodeClicked = {
            val intent = Intent(context, ScanBarcodeActivity::class.java)
            barcodeScannerLauncher.launch(intent)
        },
        detailsText = details,
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState,
        stock = stock,
        isEditMode = false,
        onBackToList = onBackToList,
        onStockChange = { stock = it },
        onSaveButtonClicked =
            {
                addViewModel.onSaveAction(
                    AddProductUiModel(
                        name = productName,
                        price = Money.toDouble(productPrice).toString(),
                        currency = currencySelected.symbol,
                        unit = unitSelected,
                        imageUri = imageUri,
                        details = details,
                        stock = stock,
                        category = uiState.category,
                        barcode = barcode
                    )
                )
                onSaveAction()
            })

    if (showAddNewCategory) {
        InputTextDialogComposable(
            supportingMessage = stringResource(R.string.add_new_category),
            onDismissRequest = { showAddNewCategory = false }
        ) {
            addViewModel.onAddCategory(it)
            showAddNewCategory = false
        }

    }

    UiStateHandler(uiState, onDismiss = { addViewModel::onErrorMessageDismissed })
}