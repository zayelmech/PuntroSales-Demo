package com.imecatro.demosales.ui.sales.add.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.components.OrderOnCartComposable
import com.imecatro.demosales.ui.sales.add.components.SearchBottomSheetComposable
import com.imecatro.demosales.ui.sales.add.components.SearchEngineUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.barcode.CameraView
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.dialogs.ActionDialog
import com.imecatro.demosales.ui.theme.dialogs.DialogType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateTicketComposable(
    productsOnCart: List<ProductOnCartUiModel>,
    ticketSubtotal: String,
    onDeleteProduct: (Long) -> Unit,
    onProductPlusClicked: (ProductOnCartUiModel) -> Unit = {},
    onProductMinusClicked: (ProductOnCartUiModel) -> Unit = {},
    onQtyValueChange: (ProductOnCartUiModel, String) -> Unit = { _, _ -> },
    onSaveAsDraftTicketClicked: () -> Unit = {},
    onBarcodeScanned: (String) -> Unit = {},
    viewScanner : Boolean = false,
    onContinueTicketClicked: () -> Unit = {},
    onAddProductClicked: () -> Unit = {}
) {

    var enableScanner by remember { mutableStateOf(true) }
    var scannedCode by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(productsOnCart) { index, item ->
                val dismissState = rememberSwipeToDismissBoxState()
                val scope = rememberCoroutineScope()

                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    ActionDialog(
                        dialogType = DialogType.Delete,
                        message =
                            stringResource(R.string.delete_item_message, item.product.name),
                        onDismissRequest = {
                            scope.launch {
                                dismissState.reset()
                            }
                        },
                        onConfirmClicked = {
                            scope.launch {
                                onDeleteProduct(item.orderId)
                                dismissState.reset()
                            }
                        })
                }

                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier
                        .padding(vertical = Dp(1f)),
                    enableDismissFromEndToStart = true,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = Dp(20f)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete Icon",
                            )
                        }
                    },
                    content = {
                        OrderOnCartComposable(
                            product = item,
                            productPosition = index,
                            onPlusClicked = { onProductPlusClicked(item) },
                            onMinusClick = { onProductMinusClicked(item) },
                            onQtyValueChange = { newQty -> onQtyValueChange(item, newQty) },
                        )
                    })
            }

            if (productsOnCart.isEmpty()) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.message_start_sale),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = onAddProductClicked,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(imageVector = Icons.Filled.Add, null)
                        Text(text = stringResource(R.string.btn_add_product))
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }

            }
            item {
                Column(Modifier.padding(20.dp)) {
                    HorizontalDivider()
                    Row {
                        Text(stringResource(R.string.label_subtotal))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(ticketSubtotal.formatAsCurrency())
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewScanner)
                CameraView(
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    enableAnalysis = enableScanner,
                    onBarcode = { code ->
                        enableScanner = false
                        scannedCode = code
                    }
                )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onContinueTicketClicked,
                modifier = Modifier
                    .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                    .fillMaxWidth(),
                enabled = productsOnCart.isNotEmpty(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(text = stringResource(R.string.btn_continue_sale))
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row {
                Spacer(modifier = Modifier.size(48.dp)) // for center text btn
                TextButton(
                    onClick = onSaveAsDraftTicketClicked,
                    enabled = productsOnCart.isNotEmpty(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(text = stringResource(R.string.btn_save_as_draft))
                }

                val state = rememberTooltipState()
                val scope = rememberCoroutineScope()
                TooltipBox(

                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text(stringResource(R.string.tooltip_info_draft)) }
                    },
                    state = state
                ) {
                    IconButton(
                        onClick = { scope.launch { state.show() } },
                        enabled = productsOnCart.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info"
                        )
                    }
                }
            }


        }

        LaunchedEffect(enableScanner, scannedCode) {
            if (!enableScanner && scannedCode.isNotBlank()) {
                onBarcodeScanned(scannedCode)
                scope.launch {
                    delay(500)
                    enableScanner = true
                    scannedCode = ""
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketComposableStateImpl(
    addSaleViewModel: AddSaleViewModel,
    onBackToList: () -> Unit = {},
    onNavigateToCheckout: (Long) -> Unit
) {
    val resultsList by addSaleViewModel.productsFound.collectAsState()
    val productsOnCart by addSaleViewModel.cartList.collectAsState()
    val ticketState by addSaleViewModel.uiState.collectAsState()

    var query by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        // Add this line to fix the error:
        confirmValueChange = { true }, // Add a default confirmValueChange lambda
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    var isModeScanner by remember {  mutableStateOf(false)}

    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.top_bar_new_sale)) },
            navigationIcon = {
                IconButton(onClick = { onBackToList() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                }
            }, actions = {
                Switch(isModeScanner, { isModeScanner = it },
                    thumbContent = { Icon(painterResource(R.drawable.barcode), null) })
            }
        )
        CreateTicketComposable(
            productsOnCart = productsOnCart.toMutableStateList(),
            ticketSubtotal = ticketState.subtotal,
            onDeleteProduct = { addSaleViewModel.onDeleteProductFromTicketAction(it) },
            onProductMinusClicked = { product ->
                addSaleViewModel.onQtyValueChangeAtPos(product, "-1")
            },
            onProductPlusClicked = { product ->
                addSaleViewModel.onQtyValueChangeAtPos(product, "+1")
            },
            onQtyValueChange = { product, number ->
                addSaleViewModel.onQtyValueChangeAtPos(product, number)
            },
            onBarcodeScanned = {
                addSaleViewModel.onSearchBarcode(it)
            },
            viewScanner = isModeScanner,
            onSaveAsDraftTicketClicked = {
                scope.launch {
                    addSaleViewModel.onSaveTicketAction()
                    onBackToList()
                }
            },
            onContinueTicketClicked = { onNavigateToCheckout(addSaleViewModel.ticketId) },
            onAddProductClicked = { scope.launch { openBottomSheet = true } }
        )
    }

    val context = LocalContext.current
    LaunchedEffect(ticketState) {
        if (ticketState.productNotFount == true){
            scope.launch {
                Toast.makeText(context, "Product Not found", Toast.LENGTH_SHORT).show()
                addSaleViewModel.updateState { copy(productNotFount = false) }

            }
        }
    }

    if (openBottomSheet)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openBottomSheet = false }, content = {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val searchUiState = SearchEngineUiModel(
                        list = resultsList.toMutableStateList(),
                        query = query,
                        onQueryChange = {
                            query = it
                            addSaleViewModel.onSearchProductAction(query)
                        }
                    )
                    SearchBottomSheetComposable(
                        searchUiState,
                        onDeductProductClicked = {
                            addSaleViewModel.onDeductProductWithId(it.id)
                        },
                        onAddProductClicked = {
                            addSaleViewModel.onAddProductToCartAction(it)
                        }
                    )
                }
            })

    DisposableEffect(key1 = Unit) {
        onDispose {
            if (productsOnCart.isEmpty()) addSaleViewModel.onCancelTicketAction()
            else addSaleViewModel.onSaveTicketAction()
        }
    }


    // User confirmation dialog

}

private fun createFakeListOfProductsOnCart(num: Int): List<ProductOnCartUiModel> {
    val list: MutableList<ProductOnCartUiModel> = mutableListOf()
    for (i in 1L..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1.0,
            imageUri = null
        )
        val new2 =
            ProductOnCartUiModel(
                orderId = 0,
                product = new,
                qty = 0.0,
                subtotal = 1.0f.toBigDecimal()
            )

        list.add(new2)
    }
    return list
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewCreateTicketComposable() {
    PuntroSalesDemoTheme(darkTheme = false) {
        Surface(
//            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTicketComposable(
                createFakeListOfProductsOnCart(5),
                "0.00",
                {},
                {},
                {},
                { _, _ -> }, {}, {}
            )

        }
    }
}

