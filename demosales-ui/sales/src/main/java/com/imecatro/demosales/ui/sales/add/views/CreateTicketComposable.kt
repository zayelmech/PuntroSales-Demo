package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import kotlinx.coroutines.launch

@Composable
fun CreateTicketComposable(
    productsOnCart: List<ProductOnCartUiModel>,
    ticketSubtotal: String,
    onDeleteProduct: (Long) -> Unit,
    onProductPlusClicked: (ProductOnCartUiModel) -> Unit,
    onProductMinusClicked: (ProductOnCartUiModel) -> Unit,
    onQtyValueChange: (ProductOnCartUiModel, String) -> Unit,
    onContinueTicketClicked: () -> Unit,
    onAddProductClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("New Sale", style = MaterialTheme.typography.titleMedium)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            itemsIndexed(productsOnCart) { index, item ->
                val dismissState = rememberSwipeToDismissBoxState()
                val scope = rememberCoroutineScope()

                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    OnDeleteItemDialog(item.product.name ?: "unknown", {
                        scope.launch {
                            dismissState.reset()
                        }
                    }) {
                        scope.launch {
                            onDeleteProduct(item.orderId)
                            dismissState.reset()
                        }
                    }
                }

                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier
                        .padding(vertical = Dp(1f)),
                    enableDismissFromEndToStart = true,
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
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {

                        Text(
                            text = "Add products to this section, when is done press Continue.\n\nNote: if you need to delete a product, just swipe to left the item",
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
                        Text(text = "Add product", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }

            }
            item {
                Column(Modifier.padding(20.dp)) {
                    HorizontalDivider()
                    Row {
                        Text("Subtotal")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(ticketSubtotal)
                    }
                }
            }
        }

        Button(
            onClick = onContinueTicketClicked,
            modifier = Modifier
                .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                .fillMaxWidth(),
            enabled = productsOnCart.isNotEmpty(),
            shape = MaterialTheme.shapes.large
        ) {
            Text(text = "Continue")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketComposableStateImpl(
    addSaleViewModel: AddSaleViewModel,
    onNavigateToCheckout: () -> Unit
) {
    val resultsList by addSaleViewModel.productsFound.collectAsState()
    val productsOnCart by addSaleViewModel.cartList.collectAsState()
    val ticketSubtotal by addSaleViewModel.ticketSubtotal.collectAsState()

    val ticketState by addSaleViewModel.ticketState.collectAsState()
    var query by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()


    BottomSheetScaffold(scaffoldState =
    scaffoldState, sheetPeekHeight = 0.dp, sheetContent = {
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
                },
                onProductClicked = {
                    addSaleViewModel.onAddProductToCartAction(it)
                }
            )
            SearchBottomSheetComposable(searchUiState)
        }
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            CreateTicketComposable(
                productsOnCart = productsOnCart.toMutableStateList(),
                ticketSubtotal = ticketSubtotal,
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
                onContinueTicketClicked = { onNavigateToCheckout() },
                onAddProductClicked = { scope.launch { scaffoldState.bottomSheetState.expand() } }
            )
        }

    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            if (productsOnCart.isEmpty()) addSaleViewModel.onCancelTicketAction()
            else addSaleViewModel.onSaveTicketAction()
        }
    }
}

fun createFakeListOfProductsOnCart(num: Int): List<ProductOnCartUiModel> {
    val list: MutableList<ProductOnCartUiModel> = mutableListOf()
    for (i in 1..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1f,
            imageUri = null
        )
        val new2 =
            ProductOnCartUiModel(
                orderId = 0,
                product = new,
                qty = 0f,
                subtotal = 1.0f.toBigDecimal()
            )

        list.add(new2)
    }
    return list
}

@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun PreviewCreateTicketComposable() {
    PuntroSalesDemoTheme(darkTheme = false) {
        Surface(
//            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTicketComposable(
                createFakeListOfProductsOnCart(5),
                "",
                {},
                {},
                {},
                { _, _ -> }, {}, {}
            )

        }
    }
}

