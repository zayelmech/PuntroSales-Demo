package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.sales.add.viewmodel.EditDialogUiState
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.dialogs.InputNumberDialogComposable


@Composable
fun CheckoutTicketComposable(
    client: String,
    onChangeClientClick: () -> Unit,
    note: String,
    onNoteTextChange: (String) -> Unit,
    subtotal: String,
    extra: String,
    onExtraClick: () -> Unit,
    total: String,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        //Cliente // search //guest
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Client", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onChangeClientClick() }) {
                Icon(Icons.Default.AccountCircle, null)
            }
            Text(text = client)

        }
        //Notas

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


            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${total}")

            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .sizeIn(maxWidth = 320.dp, minHeight = 50.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(text = "Checkout")
            }
        }
    }
}

@Composable
fun CheckoutTicketComposableImpl(
    checkoutViewModel: CheckoutViewModel,
    onCheckoutClick: () -> Unit
) {
    val ticket by checkoutViewModel.currentTicket.collectAsState()
    val showEditInputDialog by checkoutViewModel.showEditDialog.collectAsState()
    val editableField by checkoutViewModel.editableState.collectAsState()

    CheckoutTicketComposable(
        client = ticket.clientName,
        onChangeClientClick = {
            checkoutViewModel.onShowEditInputDialog(
                EditDialogUiState.Client(
                    ticket.clientName
                )
            )
        },
        note = ticket.note,
        onNoteTextChange = { checkoutViewModel.onNoteChangeAction(it) },
        subtotal = "${ticket.totals.shippingCost}",
//        onChangeShippingCostClick = {
//            checkoutViewModel.onShowEditInputDialog(
//                EditDialogUiState.Shipping(
//                    ticket.totals.shippingCost
//                )
//            )
//        },
        extra = "${ticket.totals.extra}",
        onExtraClick = { checkoutViewModel.onShowEditInputDialog(EditDialogUiState.Extra(ticket.totals.extra)) },
        total = "${ticket.totals.total}",
        onCheckoutClick = onCheckoutClick
    )
    if (showEditInputDialog) {
        InputNumberDialogComposable(
            initialValue = editableField.txt,
            onDismissRequest = { /*TODO*/ },
            onConfirmClicked = { checkoutViewModel.onEditDialogConfirmation(editableField, it) }
        )
    }

}


@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun PreviewCheckoutTicketComposable() {
    PuntroSalesDemoTheme {
        Surface {
            CheckoutTicketComposable(
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