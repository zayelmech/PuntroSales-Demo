package com.imecatro.demosales.ui.sales.add.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.components.SearchClientBottomSheet
import com.imecatro.demosales.ui.sales.add.components.SearchClientEngineModel
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.dialogs.InputNumberDialogComposable
import kotlinx.coroutines.launch


@Composable
fun CheckoutTicketComposable(
    saleId: Long,
    client: String,
    onChangeClientClick: () -> Unit,
    note: String,
    onNoteTextChange: (String) -> Unit,
    subtotal: String,
    extra: String,
    onExtraClick: () -> Unit,
    total: String,
    onCheckoutClick: () -> Unit,
    onSavePending: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(text = "Sale #$saleId", style = MaterialTheme.typography.titleLarge)
        //Cliente // search //guest
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Client", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onChangeClientClick() }) {
                Icon(Icons.Default.AccountCircle, null)
            }
            Text(text = client)

        }
        //Notes
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.weight(1f))
        }
        OutlinedTextField(
            value = note,
            onValueChange = onNoteTextChange,
            singleLine = false,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(40.dp))

        Column {
            HorizontalDivider()
            Row {
                Text("Subtotal")
                Spacer(modifier = Modifier.weight(1f))
                Text("$$subtotal")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onExtraClick() }) {
                    Icon(Icons.Default.AddCircle, null)
                }
                Text("$$extra")
            }

//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "Discount")
//                Spacer(modifier = Modifier.weight(1f))
//                IconButton(onClick = { onExtraClick() }) {
//                    Icon(Icons.Default.AddCircle, null)
//                }
//                Text("$$extra")
//            }

            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${total}")

            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(text = "Checkout")
            }
            TextButton(
                onClick = onSavePending,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = "Save as pending sale")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutTicketComposableImpl(
    checkoutViewModel: CheckoutViewModel,
    onTicketCheckedOut: (Long) -> Unit
) {
    val uiState by checkoutViewModel.uiState.collectAsState()

    var showEditInputDialog by remember {
        mutableStateOf(false)
    }

    val resultsList by checkoutViewModel.clientsFound.collectAsState()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    var query by remember {
        mutableStateOf("")
    }

    BottomSheetScaffold(
        scaffoldState =
            scaffoldState, sheetPeekHeight = 0.dp, sheetContent = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val searchUiState = SearchClientEngineModel(
                    list = resultsList.toMutableStateList(),
                    query = query,
                    onQueryChange = {
                        query = it
                        checkoutViewModel.onSearchClientAction(query)
                    },
                    onClientClicked = {
                        checkoutViewModel.onAddClientAction(it)
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                )
                SearchClientBottomSheet(searchUiState)
            }
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            CheckoutTicketComposable(
                saleId = uiState.ticket.id,
                client = uiState.ticket.clientName,
                onChangeClientClick = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                note = uiState.ticket.note,
                onNoteTextChange = { checkoutViewModel.onNoteChangeAction(it) },
                subtotal = "${uiState.ticket.totals.subtotal}",
                extra = "${uiState.ticket.totals.extra}",
                onExtraClick = { showEditInputDialog = true },
                total = "${uiState.ticket.totals.total}",
                onCheckoutClick = {
                    checkoutViewModel.onCheckoutAction()
                },
                onSavePending = { checkoutViewModel.onSavePendingTicked()}
            )
        }
    }

    LaunchedEffect(uiState.ticket.ticketSaved) {
        if (uiState.ticket.ticketSaved) onTicketCheckedOut(uiState.ticket.id)
    }
    if (showEditInputDialog) {
        InputNumberDialogComposable(
            initialValue = "",
            onDismissRequest = { showEditInputDialog = false },
            onConfirmClicked = {
                checkoutViewModel.onExtraChargeAdded(it)
                showEditInputDialog = false
            })
    }

}


@Preview(
    showBackground = true, device = "spec:width=411dp,height=891dp"
)
@Composable
fun PreviewCheckoutTicketComposable() {
    PuntroSalesDemoTheme {
        Surface {
            CheckoutTicketComposable(
                saleId = 1,
//                createFakeListOfProductsOnCart(50),
                client = "unknown",
                onChangeClientClick = {},
                note = "this client",
                onNoteTextChange = {},
                subtotal = "0.0",
                extra = "0.0",
                onExtraClick = {/*TODO dialog*/ },
                total = "0.0",
                onCheckoutClick = { }

            )
        }
    }
}