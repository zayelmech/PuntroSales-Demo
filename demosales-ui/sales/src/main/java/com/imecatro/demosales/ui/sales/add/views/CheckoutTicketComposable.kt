package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.sales.add.viewmodel.EditDialogUiState
import com.imecatro.demosales.ui.theme.ButtonFancy
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme


@Composable
fun CheckoutTicketComposable(
    client: String,
    onChangeClientClick: () -> Unit,
    note: String,
    onNoteTextChange: (String) -> Unit,
    shipping: String,
    onChangeShippingCostClick: () -> Unit,
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

            Text(text = "Client", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onChangeClientClick) {
                Text(text = client)
            }
        }
        //Notas

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes", style = MaterialTheme.typography.labelMedium)
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
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            //Shipping cost
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(text = "Shipping", style = MaterialTheme.typography.labelMedium)
//                Spacer(modifier = Modifier.weight(1f))
//                TextButton(onClick = { onChangeShippingCostClick() }) {
//                    Text(text = "$$shipping")
//                }
//            }

            //Extra
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { onExtraClick() }) {
                    Text(text = "$$extra")
                }
            }
            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${total}", style = MaterialTheme.typography.bodyMedium)

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ButtonFancy(
            text = "Checkout",
            paddingX = 0.dp,
            icon = Icons.Filled.Done,
            onClicked = onCheckoutClick
        )
        //State , save as
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
        shipping = "${ticket.totals.shippingCost}",
        onChangeShippingCostClick = {
            checkoutViewModel.onShowEditInputDialog(
                EditDialogUiState.Shipping(
                    ticket.totals.shippingCost
                )
            )
        },
        extra = "${ticket.totals.extra}",
        onExtraClick = { checkoutViewModel.onShowEditInputDialog(EditDialogUiState.Extra(ticket.totals.extra)) },
        total = "${ticket.totals.total}",
        onCheckoutClick = onCheckoutClick
    )
    if (showEditInputDialog) {
        InputNumberDialogComposable(
            initialValue = editableField.txt,
            onDismissRequest = { /*TODO*/ },
            onConfirmClicked = { checkoutViewModel.onEditDialogConfirmation(editableField,it) }
        )
    }

}


@Preview(showBackground = true,
    device = "spec:id=reference_tablet,shape=Normal,width=1280,height=800,unit=dp,dpi=240"
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
                shipping = "0.0",
                onChangeShippingCostClick = {},
                extra = "0.0",
                onExtraClick = {/*TODO dialog*/ },
                total = "0.0",
                onCheckoutClick = { }

            )
        }
    }
}