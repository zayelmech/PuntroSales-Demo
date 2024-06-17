package com.imecatro.products.ui.add.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.PurpleGrey40
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.viewmodel.AddViewModel
import com.imecatro.demosales.ui.theme.ButtonFancy
import com.imecatro.demosales.ui.theme.DropListPicker
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage


@OptIn(ExperimentalMaterial3Api::class)
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
    detailsText: String,
    onDetailsChange: (String) -> Unit,
    buttonSaveState: Boolean,
    onSaveButtonClicked: () -> Unit
) {

    val context = LocalContext.current

    LazyColumn {
        item {
            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = "Image", style = Typography.labelMedium, color = PurpleGrey40)
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

                Text(text = "Product name", style = Typography.labelMedium, color = PurpleGrey40)
                OutlinedTextField(value = productName, onValueChange = onProductNameChange)

                Text(text = "Price", style = Typography.labelMedium, color = PurpleGrey40)
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
                Text(text = "Unit", style = Typography.labelMedium, color = PurpleGrey40)


                DropListPicker(
                    unitList, unitPicked
                ) { unitPicked ->
                    onUnitPicked(unitPicked)
                }


                //Details
                Text(text = "Details", style = Typography.labelMedium, color = PurpleGrey40)
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

    var buttonEnableState by remember {
        mutableStateOf(false)
    }

    if (productName.isNotEmpty() && productPrice.isNotEmpty()) {
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
        buttonSaveState = buttonEnableState
    ) {
        addViewModel.onSaveAction(
            AddProductUiModel(
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
                buttonSaveState = false
            ) {

            }
        }
    }
}