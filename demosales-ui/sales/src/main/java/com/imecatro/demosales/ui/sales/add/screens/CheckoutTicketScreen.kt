package com.imecatro.demosales.ui.sales.add.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.components.SearchClientBottomSheet
import com.imecatro.demosales.ui.sales.add.components.SearchClientEngineModel
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.common.CurrencyVisualTransformation
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.dialogs.InputNumberDialogComposable
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutTicketComposable(
    saleId: Long,
    client: String,
    onChangeClientClick: () -> Unit,
    note: String,
    onNoteTextChange: (String) -> Unit,
    subtotal: String,
    extra: String,
    onExtraChange: (String) -> Unit,
    discount: String,
    onDiscountChange: (String) -> Unit,
    total: String,
    onCheckoutClick: () -> Unit,
    onSavePending: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(title = { Text(text = "Sale #$saleId") })
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
            Row(
                modifier = Modifier.sizeIn(minHeight = 55.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subtotal")
                Spacer(modifier = Modifier.weight(1f))
                Text(subtotal.formatAsCurrency())
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra")
                Spacer(modifier = Modifier.weight(1f))

                OutlinedTextField(
                    modifier = Modifier
                        .sizeIn(maxWidth = 150.dp),
                    value = extra,
                    onValueChange = onExtraChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    visualTransformation = CurrencyVisualTransformation(),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.size(5.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "Discount")
//                Spacer(modifier = Modifier.weight(1f))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .sizeIn(maxWidth = 150.dp),
//                    value = discount,
//                    onValueChange = onDiscountChange,
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
//                    visualTransformation = CurrencyVisualTransformation(),
//                    singleLine = true
//                )
//            }
            Spacer(modifier = Modifier.size(10.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.size(10.dp))
            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = total.formatAsCurrency())

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

    val resultsList by checkoutViewModel.clientsFound.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        // Add this line to fix the error:
        confirmValueChange = { true }, // Add a default confirmValueChange lambda
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    var query by remember {
        mutableStateOf("")
    }

    var extra by remember {
        mutableStateOf("0")
    }

    var discount by remember {
        mutableStateOf("0")
    }

    LaunchedEffect(extra) {
        snapshotFlow { extra }
            .debounce(300)
            .collect {
                val extraCharge = Money.toDouble(it)
                checkoutViewModel.onExtraChargeAdded(extraCharge.toString())
            }
    }
    CheckoutTicketComposable(
        saleId = uiState.ticket.id,
        client = uiState.ticket.clientName,
        onChangeClientClick = { scope.launch { openBottomSheet = true } },
        note = uiState.ticket.note,
        onNoteTextChange = { checkoutViewModel.onNoteChangeAction(it) },
        subtotal = "${uiState.ticket.totals.subtotal}",
        extra = extra,
        onExtraChange = { extra = it },
        discount = discount,
        onDiscountChange = { discount = it },
        total = "${uiState.ticket.totals.total}",
        onCheckoutClick = {
            checkoutViewModel.onCheckoutAction()
        },
        onSavePending = { checkoutViewModel.onSavePendingTicked() }
    )

    if (openBottomSheet)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openBottomSheet = false },
            content = {
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
                            scope.launch { openBottomSheet = false }
                        }
                    )
                    SearchClientBottomSheet(searchUiState)
                }
            })
    LaunchedEffect(uiState.ticket.ticketSaved) {
        if (uiState.ticket.ticketSaved) onTicketCheckedOut(uiState.ticket.id)
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
                onExtraChange = {/*TODO dialog*/ },
                total = "0.0",
                onCheckoutClick = { },
                discount = "0.0",
                onDiscountChange = {},
                onSavePending = {}
            )
        }
    }
}