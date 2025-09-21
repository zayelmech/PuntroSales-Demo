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
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.createImageFile
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.demosales.ui.theme.dialogs.InputTextDialogComposable
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.views.AddProductComposable
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel

@Composable
fun UpdateProductComposableStateImpl(
    updateProductViewModel: UpdateProductViewModel,
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

    var productName by remember {
        mutableStateOf(uiState.productDetails?.name)
    }
    var productPrice by remember {
        mutableStateOf(uiState.productDetails?.price)
    }

    var currencySelected by remember {
        mutableStateOf(uiState.productDetails?.currency)
    }
    var unitSelected by remember {
        mutableStateOf(uiState.productDetails?.unit)
    }

    var showAddNewCategory by remember {
        mutableStateOf(false)
    }

    var details by remember {
        mutableStateOf(uiState.productDetails?.details)
    }

    val buttonEnableState by remember {
        mutableStateOf(true)
    }

    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        onTakePhoto = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        productName = productName ?: "Loading...",
        onProductNameChange = { productName = it },
        productPrice = productPrice ?: "0.00",
        onProductPriceChange = { productPrice = it },
        currencyPicked = currencySelected ?: "USD",
        onCurrencyChange = { currencySelected = it },
        unitList = updateProductViewModel.getUnities(),
        unitPicked = unitSelected ?: "pz",
        onUnitPicked = { unitSelected = it },
        categories = uiState.categories,
        categoryPicked = uiState.productDetails?.category?:"",
        onCategoryPicked = { updateProductViewModel.onCategoryPicked(it) },
        onAddNewCategory = { showAddNewCategory = true },
        detailsText = details ?: "",
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState,
        stock = "${uiState.productDetails?.stock ?: 0f}",
        onStockChange = {},
        onBackToList = onBackToList,
        isEditMode = true
    ) {
        updateProductViewModel.onSaveAction(
            UpdateProductUiModel(
                id = 0,
                name = productName,
                price = Money.toDouble(productPrice?:"0.0").toString(),
                currency = currencySelected,
                unit = unitSelected,
                imageUri = imageUri,
                stock = 0f,
                details = details ?: "",
                category = uiState.productDetails?.category
            )
        )
    }

    if (showAddNewCategory) {
        InputTextDialogComposable(
            supportingMessage = stringResource(R.string.add_new_category)
        ) {
            updateProductViewModel.onAddCategory(it)
            showAddNewCategory = false
        }

    }

    UiStateHandler(uiState) {
        // TODO back
    }

    LaunchedEffect(uiState) {
        if (uiState.productUpdated){
            onSaveAction.invoke()
        }

        if(uiState.productDetails != null){
            productName = uiState.productDetails!!.name
            productPrice = uiState.productDetails!!.price
            currencySelected = uiState.productDetails!!.currency
            unitSelected = uiState.productDetails!!.unit
            imageUri = uiState.productDetails!!.imageUri
            details = uiState.productDetails!!.details
        }

    }
}