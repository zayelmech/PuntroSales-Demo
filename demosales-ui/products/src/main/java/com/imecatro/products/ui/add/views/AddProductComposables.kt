package com.imecatro.products.ui.add.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.ButtonFancy
import com.imecatro.demosales.ui.theme.DropListPicker
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.viewmodel.AddViewModel


@Composable
fun AddProductComposable(
    uri: Uri?,
    onPickImage: () -> Unit,
    productName: String,
    onProductNameChange: (String) -> Unit,
    productPrice: String,
    onProductPriceChange: (String) -> Unit,
    currencyList: List<String>,
    currencyPicked: String,
    onCurrencyChange: (String) -> Unit,
    unitList: List<String>,
    unitPicked: String,
    onUnitPicked: (String) -> Unit,
    stock: String,
    onStockChange: (String) -> Unit,
    isEditMode : Boolean,
    detailsText: String,
    onDetailsChange: (String) -> Unit,
    buttonSaveState: Boolean,
    onSaveButtonClicked: () -> Unit
) {

    val context = LocalContext.current

    LazyColumn {
        item {
            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = "Image", style = Typography.labelMedium)
                Box(

                    modifier = Modifier
                        .clickable {
                            onPickImage()
                        }
                        .size(100.dp)
                        .wrapContentSize(Alignment.Center)

                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(uri)
                                .error(R.drawable.baseline_add_photo_alternate_24)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .clip(RoundedCornerShape(25)),
                        contentScale = ContentScale.FillWidth
                    )

                }

                Text(text = "Product name", style = Typography.labelMedium)
                OutlinedTextField(value = productName, onValueChange = onProductNameChange)

                Text(text = "Price", style = Typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = onProductPriceChange,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )


                    DropListPicker(
                        currencyList, currencyPicked
                    ) { currency ->
                        onCurrencyChange(currency)
                    }

                }
                Text(text = "Unit", style = Typography.labelMedium)


                DropListPicker(
                    unitList, unitPicked
                ) { unitPicked ->
                    onUnitPicked(unitPicked)
                }
                Text(text = "Stock", style = Typography.labelMedium)
                OutlinedTextField(
                    enabled = !isEditMode,
                    value = stock,
                    onValueChange = onStockChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                //Details
                Text(text = "Details", style = Typography.labelMedium)
                HorizontalDivider(
                    modifier = Modifier.padding(0.dp, 5.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )


                OutlinedTextField(
                    value = detailsText,
                    onValueChange = onDetailsChange,
                    singleLine = false,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(60.dp))
                ButtonFancy(
                    text = "SAVE",
                    paddingX = 0.dp,
                    icon = Icons.Filled.Done,
                    enable = buttonSaveState
                ) {
                    onSaveButtonClicked()
                }
            }
        }
    }

}


@Composable
fun AddProductComposableStateImpl(addViewModel: AddViewModel, onSaveAction: () -> Unit) {

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


    var productName by remember {
        mutableStateOf("")
    }
    var productPrice by remember {
        mutableStateOf("")
    }

    var currencySelected by remember {
        mutableStateOf("USD")
    }
    var unitSelected by remember {
        mutableStateOf("pz")
    }
    var details by remember {
        mutableStateOf("")
    }

    var stock by remember {
        mutableStateOf("0")
    }

    var buttonEnableState by remember {
        mutableStateOf(false)
    }

    if (productName.isNotEmpty() && productPrice.isNotEmpty() && stock.isNotBlank()) {
        buttonEnableState = true
    }



    AddProductComposable(
        uri = imageUri,
        onPickImage = { launcher.launch("image/*") },
        productName = productName,
        onProductNameChange = { productName = it },
        productPrice = productPrice,
        onProductPriceChange = { productPrice = it.filter { c -> c.isDigit() || c == '.' } },
        currencyList = addViewModel.getCurrencies(),
        currencyPicked = currencySelected,
        onCurrencyChange = { currencySelected = it },
        unitList = addViewModel.getUnities(),
        unitPicked = unitSelected,
        onUnitPicked = { unitSelected = it },
        detailsText = details,
        onDetailsChange = { details = it },
        buttonSaveState = buttonEnableState,
        stock = stock,
        isEditMode = false,
        onStockChange = { stock = it }
    ) {
        addViewModel.onSaveAction(
            AddProductUiModel(
                name = productName,
                price = productPrice,
                currency = currencySelected,
                unit = unitSelected,
                imageUri = imageUri,
                details = details,
                stock = stock
            )
        )
        onSaveAction()
    }
}


@Preview(showBackground = true)
@Composable
fun AddProductComposablePreview() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddProductComposable(
                uri = null,
                onPickImage = { /*TODO*/ },
                productName = "",
                onProductNameChange = { },
                productPrice = "",
                onProductPriceChange = {},
                currencyList = listOf("USD", "MXN", "EUR", "GBP"),
                currencyPicked = "USD",
                onCurrencyChange = {},
                unitList = listOf("pz", "kg", "g", "m", "cm"),
                unitPicked = "pz",
                onUnitPicked = {},
                detailsText = "",
                onDetailsChange = {},
                buttonSaveState = false,
                stock = "1",
                onStockChange = {},
                isEditMode = false
            ) {

            }
        }
    }
}