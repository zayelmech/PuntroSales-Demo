package com.imecatro.products.ui.update.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.imecatro.products.ui.add.views.AddProductComposable
import com.imecatro.products.ui.update.model.UpdateProductUiModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel

@Composable
fun UpdateProductComposableStateImpl(updateProductViewModel: UpdateProductViewModel, productId : Int?, onSaveAction: () -> Unit ) {

    val updateProductUiModel : UpdateProductUiModel by remember {
        mutableStateOf(updateProductViewModel.getProductById(productId))
    }

    var imageUri by remember {

        mutableStateOf<Uri?>(updateProductUiModel.imageUri)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uriPicked: Uri? ->
        imageUri = uriPicked
    }

    var productName by remember {
        mutableStateOf(updateProductUiModel.name)
    }
    var productPrice by remember {
        mutableStateOf(updateProductUiModel.price)
    }

    var currencySelected by remember {
        mutableStateOf(updateProductUiModel.currency)
    }
    var unitSelected by remember {
        mutableStateOf(updateProductUiModel.unit)
    }
    var details by remember {
        mutableStateOf(updateProductUiModel.details)
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
        productName = productName?:"No name",
        onProductNameChange = { productName = it },
        productPrice = productPrice?:"0.00",
        onProductPriceChange = { productPrice = it },
        currencyList = updateProductViewModel.getCurrencies(),
        onCurrencyChange = { currencySelected = it },
        unitList = updateProductViewModel.getUnities(),
        onUnitPicked = { unitSelected = it },
        detailsText = details,
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState
    ) {
        updateProductViewModel.onSaveAction(
            UpdateProductUiModel(
                id= updateProductUiModel.id,
                name = productName,
                price = productPrice,
                currency = currencySelected,
                unit = unitSelected,
                imageUri = imageUri,
                details = details
            )
        )
        onSaveAction()
    }
}