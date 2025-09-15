package com.imecatro.products.ui.update.views

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
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.products.ui.add.views.AddProductComposable
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel

@Composable
fun UpdateProductComposableStateImpl(
    updateProductViewModel: UpdateProductViewModel,
    onBackToList: () -> Unit,
    onSaveAction: () -> Unit
) {

    val updateProductUiModel by updateProductViewModel.productSelected.collectAsState()
    val uiState by updateProductViewModel.uiState.collectAsState()

    var imageUri by remember {
        mutableStateOf<Uri?>(updateProductUiModel?.imageUri)
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

    var productName by remember {
        mutableStateOf(updateProductUiModel?.name)
    }
    var productPrice by remember {
        mutableStateOf(updateProductUiModel?.price)
    }

    var currencySelected by remember {
        mutableStateOf(updateProductUiModel?.currency)
    }
    var unitSelected by remember {
        mutableStateOf(updateProductUiModel?.unit)
    }
    var details by remember {
        mutableStateOf(updateProductUiModel?.details)
    }

    val buttonEnableState by remember {
        mutableStateOf(true)
    }

    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        productName = productName ?: "Loading...",
        onProductNameChange = { productName = it },
        productPrice = productPrice ?: "0.00",
        onProductPriceChange = { productPrice = it },
        currencyList = updateProductViewModel.getCurrencies(),
        currencyPicked = currencySelected ?: "USD",
        onCurrencyChange = { currencySelected = it },
        unitList = updateProductViewModel.getUnities(),
        unitPicked = unitSelected ?: "pz",
        onUnitPicked = { unitSelected = it },
        detailsText = details ?: "",
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState,
        stock = "${updateProductUiModel?.stock ?: 0f}",
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
                details = details ?: ""
            )
        )
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