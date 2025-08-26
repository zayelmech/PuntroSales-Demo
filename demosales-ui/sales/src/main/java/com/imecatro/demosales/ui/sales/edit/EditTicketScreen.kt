package com.imecatro.demosales.ui.sales.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.components.SearchBottomSheetComposable
import com.imecatro.demosales.ui.sales.add.components.SearchEngineUiModel
import com.imecatro.demosales.ui.sales.add.screens.CreateTicketComposable
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketComposableStateImpl(
    addSaleViewModel: EditSaleViewModel,
    onNavigateToCheckout: (Long) -> Unit
) {
    val resultsList by addSaleViewModel.productsFound.collectAsState()
    val productsOnCart by addSaleViewModel.cartList.collectAsState()
    val ticketSubtotal by addSaleViewModel.ticketSubtotal.collectAsState()

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
                onContinueTicketClicked = { onNavigateToCheckout(addSaleViewModel.saleId) },
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
