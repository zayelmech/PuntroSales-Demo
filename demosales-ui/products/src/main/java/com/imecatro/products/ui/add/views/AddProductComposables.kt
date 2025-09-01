package com.imecatro.products.ui.add.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.ButtonFancy
import com.imecatro.demosales.ui.theme.DropListPicker
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.demosales.ui.theme.common.saveMediaToStorage
import com.imecatro.products.ui.R
import com.imecatro.products.ui.add.model.AddProductUiModel
import com.imecatro.products.ui.add.viewmodel.AddViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddProductComposable(
    uri: Uri? = null,
    onPickImage: () -> Unit = {},
    productName: String = "",
    onProductNameChange: (String) -> Unit = {},
    productPrice: String = "",
    onProductPriceChange: (String) -> Unit = {},
    currencyList: List<String> = emptyList(),
    currencyPicked: String = "",
    onCurrencyChange: (String) -> Unit = {},
    unitList: List<String> = emptyList(),
    unitPicked: String = "",
    onUnitPicked: (String) -> Unit = {},
    stock: String = "",
    onStockChange: (String) -> Unit = {},
    isEditMode: Boolean = false,
    detailsText: String = "",
    onDetailsChange: (String) -> Unit = {},
    buttonSaveState: Boolean = false,
    onBackToList: () -> Unit = {},
    onSaveButtonClicked: () -> Unit = {}
) {

    val context = LocalContext.current

    LazyColumn {
        item {
            TopAppBar(
                title = { Text(text = if (!isEditMode) "New Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = { onBackToList() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                }
            )
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
                OutlinedTextField(
                    value = productName,
                    supportingText = { if (productName.isBlank()) Text(stringResource(R.string.supporting_name_txt)) },
                    onValueChange = onProductNameChange
                )

                Text(text = "Price", style = Typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = onProductPriceChange,
                        placeholder = { Text("0.0") },
                        supportingText = { if (productPrice.isBlank()) Text(stringResource(R.string.supporting_price_txt)) },
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
                    placeholder = { Text("0.0") },
                    supportingText = { if (productName.isBlank()) Text(stringResource(R.string.supporting_stock_txt)) },
                    singleLine = true,
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
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        enabled = buttonSaveState,
                        onClick = onSaveButtonClicked,
                        modifier = Modifier
                            .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Filled.Done, null)
                        Text(text = stringResource(R.string.btn_save))
                    }
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
        mutableStateOf("")
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
        onBackToList = onBackToList,
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