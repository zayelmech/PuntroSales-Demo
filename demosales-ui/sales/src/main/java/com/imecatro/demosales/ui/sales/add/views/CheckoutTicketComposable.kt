package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
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
    saleId: Long,
    onTicketCheckedOut: (Long) -> Unit
) {
    val ticket by checkoutViewModel.currentTicket.collectAsState()

    var showEditInputDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        checkoutViewModel.onGetDetailsAction(saleId)
    }
    CheckoutTicketComposable(client = ticket.clientName,
        onChangeClientClick = { /*TODO*/ },
        note = ticket.note,
        onNoteTextChange = { checkoutViewModel.onNoteChangeAction(it) },
        subtotal = "${ticket.totals.subtotal}",
        extra = "${ticket.totals.extra}",
        onExtraClick = { showEditInputDialog = true },
        total = "${ticket.totals.total}",
        onCheckoutClick = {
            checkoutViewModel.onCheckoutAction()
        })

    LaunchedEffect(ticket.ticketSaved) {
        if (ticket.ticketSaved) onTicketCheckedOut(ticket.id)
    }
    if (showEditInputDialog) {
        InputNumberDialogComposable(initialValue = "",
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