package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketComposable(
    productsOnCart: List<ProductOnCartUiModel>,
    ticketSubtotal: String,
    onDeleteProduct: (Long) -> Unit,
    onProductPlusClicked: (ProductOnCartUiModel) -> Unit,
    onProductMinusClicked: (ProductOnCartUiModel) -> Unit,
    onQtyValueChange: (ProductOnCartUiModel, String) -> Unit,
    onSaveTicketClicked: () -> Unit,
    onCheckoutClicked: () -> Unit,
    productsContent: @Composable LazyItemScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            Button(
                onClick = onSaveTicketClicked,
                modifier = Modifier.weight(1f),
                enabled = productsOnCart.isNotEmpty(),
                shape = RoundedCornerShape(20)
            ) {
                Text(text = "SAVE", color = Color.White)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = onCheckoutClicked,
                enabled = productsOnCart.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20)
            ) {
                Text(text = "CHARGE $${ticketSubtotal}", color = Color.White)
            }
        }

        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
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
                                //modifier = Modifier.scale(1f)
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
            item {
                productsContent()
               // Text(text = "a")
            }


        }

    }
}

@Composable
fun CreateTicketComposableStateImpl(
    addSaleViewModel: AddSaleViewModel,
    onSavedTicked: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val resultsList by addSaleViewModel.productsFound.collectAsState()
    val productsOnCart by addSaleViewModel.cartList.collectAsState()
    val ticketSubtotal by addSaleViewModel.ticketSubtotal.collectAsState()

    val ticketState by addSaleViewModel.ticketState.collectAsState()
    var query by remember {
        mutableStateOf("")
    }

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
        onSaveTicketClicked = { addSaleViewModel.onSaveTicketAction(); onSavedTicked() },
        onCheckoutClicked = onNavigateToCheckout
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
            ProductOnCartUiModel(orderId = 0, product = new, qty = 0f, subtotal = 0f.toBigDecimal())

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
            ) {
                SearchBottomSheetComposable()
            }

        }
    }
}

