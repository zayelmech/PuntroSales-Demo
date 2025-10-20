package com.imecatro.products.ui.update.views

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.architect.isLoading
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.createImageFile
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.demosales.ui.theme.dialogs.InputTextDialogComposable
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.views.AddProductComposable
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel

@Composable
fun UpdateProductComposableStateImpl(
    updateProductViewModel: UpdateProductViewModel,
    onEditStock: () -> Unit = {},
    onBackToList: () -> Unit,
    onSaveAction: () -> Unit
) {

    val uiState by updateProductViewModel.uiState.collectAsState()

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

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

    UiStateHandler(uiState, onDismiss = { updateProductViewModel::onErrorMessageDismissed })

    if (uiState.isLoading) return // Loading effect

    var showAddNewCategory by remember {
        mutableStateOf(false)
    }

    var editedProduct by remember {
        mutableStateOf(UpdateProductUiModel())
    }

    val buttonEnableState by remember {
        mutableStateOf(true)
    }

    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        onTakePhoto = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        productName = editedProduct.name,
        onProductNameChange = { editedProduct = editedProduct.copy(name = it) },
        productPrice = editedProduct.price,
        onProductPriceChange = { editedProduct = editedProduct.copy(price = it) },
        currencyPicked = editedProduct.currency,
        onCurrencyChange = { editedProduct = editedProduct.copy(currency = it) },
        unitList = updateProductViewModel.getUnities(),
        unitPicked = editedProduct.unit,
        onUnitPicked = { editedProduct = editedProduct.copy(unit = it) },
        categories = uiState.categories,
        categoryPicked = uiState.productDetails?.category?:"",
        onCategoryPicked = { updateProductViewModel.onCategoryPicked(it) },
        onAddNewCategory = { showAddNewCategory = true },
        barcode = uiState.productDetails?.barcode?:"",
        onBarcodeChange = { updateProductViewModel.onBarcodeChange(it) },
        onBarcodeClicked = { /**TODO*/},
        detailsText = editedProduct.details,
        onDetailsChange = { editedProduct = editedProduct.copy(details = it) },
        buttonSaveState = buttonEnableState,
        stock = "${editedProduct.stock}",
        onEditStock = onEditStock,
        onBackToList = onBackToList,
        isEditMode = true
    ) {
        updateProductViewModel.onSaveAction(
            UpdateProductUiModel(
                name = editedProduct.name,
                price = Money.toDouble(editedProduct.price).toString(),
                currency = editedProduct.currency,
                unit = editedProduct.unit,
                imageUri = imageUri,
                stock = editedProduct.stock,
                details = editedProduct.details,
                category = uiState.productDetails?.category,
                barcode = uiState.productDetails?.barcode
            )
        )
    }

    if (showAddNewCategory) {
        InputTextDialogComposable(
            supportingMessage = stringResource(R.string.add_new_category),
            onDismissRequest = { showAddNewCategory = false }
        ) {
            updateProductViewModel.onAddCategory(it)
            showAddNewCategory = false
        }

    }

    val product = uiState.productDetails ?: return

    val priceFormatted = product.price.formatAsCurrency() // Formatting for correction error

    LaunchedEffect(uiState) {
        if (uiState.productUpdated) {
            onSaveAction.invoke()
        }

        if (uiState.productDetails?.id != null) {
            editedProduct = editedProduct.copy(
                name = product.name,
                price = priceFormatted,
                currency = product.currency,
                unit = product.unit,
                stock = product.stock,
                details = product.details
            )
            imageUri = product.imageUri
        }
    }
}