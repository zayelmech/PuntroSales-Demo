package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.viewmodel.CheckoutViewModel
import com.imecatro.demosales.ui.sales.add.viewmodel.EditDialogUiState
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography

@Composable
fun CheckoutTicketComposable(
    list: List<ProductOnCartUiModel>,
    client: String,
    onChangeClientClick: () -> Unit,
    note: String,
    onAddNoteClick: () -> Unit,
    shipping: String,
    onChangeShippingCostClick: () -> Unit,
    tax: String,
    onChangeTaxClick: () -> Unit,
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

            Text(text = "Client", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            /*TODO implement client search tool*/
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "Guest")
            }
        }
        //Notas

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            /*TODO implement if in case user type a note*/
            if (note.isEmpty()) {

                TextButton(onClick = { onAddNoteClick() }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(text = "Add note")
                }
            } else {
                TextButton(onClick = { onAddNoteClick() }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                    Text(text = note.substring(0..10))
                }
            }
        }

        //Products
        Text(text = "Items", style = Typography.labelMedium)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                items(list) { product ->
                    Row {
                        Text(text = "${product.product.name}", modifier = Modifier.weight(3f))
                        Text(text = "x${product.qty}", modifier = Modifier.weight(1f))
                        Text(
                            text = "$${product.subtotal}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            //Envio cost
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Shipping")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { onChangeShippingCostClick() }) {
                    Text(text = "$$shipping")
                }
            }

            //Tax
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tax 16%")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { onChangeTaxClick() }) {
                    Text(text = "$${tax}")
                }
            }
            //Extra
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { onExtraClick() }) {
                    Text(text = "$$extra")
                }
            }
            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${total}")

            }
        }
        Button(onClick = onCheckoutClick, colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "Checkout", color = Color.White)
        }
        //State , save as
    }
}

@Composable
fun CheckoutTicketComposableImpl(
    productsList: List<ProductOnCartUiModel>,
    checkoutViewModel: CheckoutViewModel,
    onCheckoutClick: () -> Unit
) {
    val ticket by checkoutViewModel.currentTicket.collectAsState()
    val showEditInputDialog by checkoutViewModel.showEditDialog.collectAsState()

    CheckoutTicketComposable(
        list = productsList,
        client = ticket.clientName,
        onChangeClientClick = {
            checkoutViewModel.onShowEditInputDialog(
                EditDialogUiState.Client(
                    ticket.clientName
                )
            )
        },
        note = ticket.note,
        onAddNoteClick = { checkoutViewModel.onShowEditInputDialog(EditDialogUiState.Note(ticket.note)) },
        shipping = "${ticket.totals.shippingCost}",
        onChangeShippingCostClick = {
            checkoutViewModel.onShowEditInputDialog(
                EditDialogUiState.Shipping(
                    ticket.totals.shippingCost
                )
            )
        },
        tax = "${ticket.totals.tax}",
        onChangeTaxClick = { checkoutViewModel.onShowEditInputDialog(EditDialogUiState.Tax(ticket.totals.tax)) },
        extra = "${ticket.totals.extra}",
        onExtraClick = { checkoutViewModel.onShowEditInputDialog(EditDialogUiState.Extra(ticket.totals.extra)) },
        total = "${ticket.totals.total}",
        onCheckoutClick = onCheckoutClick
    )


}


@Preview(showBackground = true)
@Composable
fun PreviewCheckoutTicketComposable() {
    PuntroSalesDemoTheme {
        Surface {
            CheckoutTicketComposable(
                createFakeListOfProductsOnCart(50),
                client = "unknown",
                onChangeClientClick = {},
                note = "this client",
                onAddNoteClick = {},
                shipping = "0.0",
                onChangeShippingCostClick = {},
                tax = "0.0",
                onChangeTaxClick = { },
                extra = "0.0",
                onExtraClick = {/*TODO dialog*/ },
                total = "0.0",
                onCheckoutClick = { }

            )
        }
    }
}