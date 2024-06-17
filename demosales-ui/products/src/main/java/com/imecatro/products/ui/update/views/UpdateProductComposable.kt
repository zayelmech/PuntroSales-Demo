package com.imecatro.products.ui.update.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.products.ui.add.views.AddProductComposable
import com.imecatro.products.ui.update.UpdateUiState
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel

@Composable
fun UpdateProductComposableStateImpl(
    updateProductViewModel: UpdateProductViewModel,
    productId: Int?,
    onSaveAction: () -> Unit
) {

//    val updateProductUiModel : UpdateProductUiModel by remember {
//        mutableStateOf(updateProductViewModel.getProductById(productId))
//    }
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

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

//    if (productName.isNotEmpty()&& productPrice.isNotEmpty()){
//        buttonEnableState = true
//    }

    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        productName = productName ?: "Loading...",
        onProductNameChange = { productName = it },
        productPrice = productPrice ?: "0.00",
        onProductPriceChange = { productPrice = it },
        currencyList = updateProductViewModel.getCurrencies(),
        currencyPicked = currencySelected?: "USD",
        onCurrencyChange = { currencySelected = it },
        unitList = updateProductViewModel.getUnities(),
        unitPicked = unitSelected ?: "pz",
        onUnitPicked = { unitSelected = it },
        detailsText = details ?: "",
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState
    ) {
        updateProductViewModel.onSaveAction(
            UpdateProductUiModel(
                id = productId,
                name = productName,
                price = productPrice,
                currency = currencySelected,
                unit = unitSelected,
                imageUri = imageUri,
                details = details ?: ""
            )
        )
        onSaveAction()
    }
    when (uiState) {
        is UpdateUiState.Loading -> {
            updateProductViewModel.getProductById(productId)
        }
        is UpdateUiState.Success -> {
            val p = (uiState as UpdateUiState.Success).product
            productName = p.name
            productPrice = p.price
            currencySelected = p.currency
            unitSelected = p.unit
            imageUri = p.imageUri
            details = p.details
            updateProductViewModel.onProductLoaded()
        }
        is UpdateUiState.Error -> {

        }
        is UpdateUiState.Loaded -> {

        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose() {
            updateProductViewModel.onStop()
        }
    }
}