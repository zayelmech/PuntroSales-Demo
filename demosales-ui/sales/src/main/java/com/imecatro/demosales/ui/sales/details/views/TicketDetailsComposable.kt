package com.imecatro.demosales.ui.sales.details.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.theme.ButtonFancy
import com.imecatro.demosales.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun TicketDetailsComposable(
    ticketDetails: TicketDetailsUiModel = TicketDetailsUiModel(listOf()),
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        //Client
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Client:", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ticketDetails.client)
        }
        //Notes
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes:", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ticketDetails.note)
        }

        //Products
        Text(text = "Items:", style = Typography.labelMedium)
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
                items(ticketDetails.list) { product ->
                    Row {
                        Text(text = product.name, modifier = Modifier.weight(3f))
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
            //Shipping cost
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Shipping")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${ticketDetails.shippingCost}")
            }
            //Tax
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tax 16%")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${ticketDetails.tax}")
            }
            //Extra
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${ticketDetails.extra}")
            }
            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$${ticketDetails.total}")

            }
        }

        ButtonFancy(
            text = "Edit",
            color = MaterialTheme.colorScheme.primary,
            icon = Icons.Filled.Edit
        ) {
            onEditClick()
        }

        ButtonFancy(
            text = "Delete",
            color = MaterialTheme.colorScheme.secondary,
            icon = Icons.Filled.Delete
        ) {
            onDeleteClick()
        }
    }
}

@Composable
fun TicketDetailsComposableImpl(
    ticketDetailsVM: TicketDetailsViewModel,
    saleId: Long
) {

    val saleSelected by ticketDetailsVM.sale.collectAsState()

    SideEffect {
        ticketDetailsVM.onGetDetailsAction(saleId)
    }

    TicketDetailsComposable(
        ticketDetails = saleSelected
    )
}


