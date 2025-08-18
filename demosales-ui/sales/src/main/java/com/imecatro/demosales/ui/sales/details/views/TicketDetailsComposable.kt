package com.imecatro.demosales.ui.sales.details.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.details.model.ProductOnTicketUiModel
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.demosales.ui.theme.dialogs.OnDeleteItemDialog

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun TicketDetailsComposable(
    ticketDetails: TicketDetailsUiModel = TicketDetailsUiModel(
        listOf(
            ProductOnTicketUiModel(
                "a",
                1.0,
                3.0
            )
        )
    ),
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onNavToList: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
        //.background(MaterialTheme.colorScheme.background)
    ) {

        //Client
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Client:", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ticketDetails.client)
        }
        //Notes
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes: ", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ticketDetails.note)
        }

        //Products
        Text(text = "Items:", style = MaterialTheme.typography.titleSmall)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(0.5.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
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
        HorizontalDivider()
        Spacer(modifier = Modifier.size(5.dp))
//        //Shipping cost
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(text = "Shipping")
//            Spacer(modifier = Modifier.weight(1f))
//            Text(text = "$${ticketDetails.shippingCost}")
//        }
//        //Extra
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
        Spacer(modifier = Modifier.size(20.dp))

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                onClick = { onEditClick() },
                modifier = Modifier
                    .sizeIn(maxWidth = 320.dp)
                    .fillMaxWidth()
            ) {
                Text("New Sale")
            }

            if (ticketDetails.isEditable)
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, null)
                    Text("Cancel")
                }
            else
                TextButton(
                    onClick = onNavToList,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Back to Sales")
                }
        }
    }
}

@Composable
fun TicketDetailsComposableImpl(
    ticketDetailsVM: TicketDetailsViewModel,
    saleId: Long,
    onNavigateAction: (Long?) -> Unit
) {

    val saleSelected by ticketDetailsVM.sale.collectAsState()
    var showDeleteTicketDialog by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        ticketDetailsVM.onGetDetailsAction(saleId)
    }

    TicketDetailsComposable(ticketDetails = saleSelected,
        onDeleteClick = { showDeleteTicketDialog = true },
        onEditClick = { onNavigateAction(saleId) },
        onNavToList = { onNavigateAction(null) })

    if (showDeleteTicketDialog) {
        OnDeleteItemDialog(
            message = stringResource(id = R.string.delete_ticket),
            onDismissRequest = { showDeleteTicketDialog = false },
            onConfirmClicked = {
                ticketDetailsVM.onDeleteTicketAction(saleId)
                onNavigateAction(null)
            }
        )
    }


}


