package com.imecatro.demosales.ui.sales.details.views

import android.app.Activity
import android.content.ClipData
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.chip.Chip
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.BitmapComposer
import com.imecatro.demosales.ui.sales.details.model.ProductOnTicketUiModel
import com.imecatro.demosales.ui.sales.details.model.TicketDetailsUiModel
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography
import com.imecatro.demosales.ui.theme.dialogs.OnDeleteItemDialog
import kotlinx.coroutines.launch

private fun getFakeItems(): List<ProductOnTicketUiModel> {
    val lst = mutableListOf<ProductOnTicketUiModel>()
    repeat(20) {
        val new = ProductOnTicketUiModel(
            0L,
            "a",
            1.0,
            3.0
        )
        lst.add(new)
    }
    return lst
}

// -----------------------
// On-screen (uses LazyColumn)
// -----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ResumeTicketScreen(
    ticketDetails: TicketDetailsUiModel = TicketDetailsUiModel(list = getFakeItems(), statusColor = Color.Red, status = "Pending")
) {
    LazyColumn(
        modifier = Modifier
            .sizeIn(maxWidth = 410.dp, maxHeight = 4000.dp)
            .padding(20.dp, 5.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ticket: #${ticketDetails.id}", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(color = ticketDetails.statusColor, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 2.dp)
                        .sizeIn(minHeight = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = ticketDetails.status, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Client
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Client:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.weight(1f))
                Text(ticketDetails.client.name)
            }

            // Notes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notes:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.weight(1f))
            }
            if (ticketDetails.note.isNotBlank()) Text(ticketDetails.note)

            // Products
            Text("Items:", style = MaterialTheme.typography.titleSmall)
        }

        items(ticketDetails.list) { product ->
            Row(Modifier.fillMaxWidth()) {
                Text(product.name, modifier = Modifier.weight(3f))
                Text("x${product.qty}", modifier = Modifier.weight(1f))
                Text(
                    text = "$${product.subtotal}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }

        item {
            HorizontalDivider()
            Spacer(Modifier.size(5.dp))

            // Extra
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Extra")
                Spacer(Modifier.weight(1f))
                Text("$${ticketDetails.extra}")
            }
            // Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total")
                Spacer(Modifier.weight(1f))
                Text("$${ticketDetails.total}")
            }
            Spacer(Modifier.size(20.dp))


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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val bitmapComposer = remember { BitmapComposer() }

    val onShareTicket = remember {
        {
            scope.launch {

                val bmp = bitmapComposer
                    .composableToBitmap(
                        activity = context as Activity,
                        width = 420.dp, // any width you want
                        screenDensity = density,
                        content = {
                            PuntroSalesDemoTheme {
                                Surface(color = MaterialTheme.colorScheme.background) {

                                    ResumeTicketScreen(saleSelected)
                                }
                            }
                        }
                    )

                val file =
                    java.io.File(context.cacheDir, "ticket_${System.currentTimeMillis()}.png")
                file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", file
                )
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    clipData = ClipData.newUri(
                        context.contentResolver,
                        "Ticket image",
                        uri
                    ) // <-- enables preview

                }
                context.startActivity(android.content.Intent.createChooser(intent, "Share ticket"))
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Sale details", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onShareTicket.invoke() }) {
                    Icon(Icons.Default.Share, contentDescription = "Share ticket")
                }
            }
        }
        HorizontalDivider()
        Column(Modifier.weight(1f)) {
            ResumeTicketScreen(ticketDetails = saleSelected)
        }
        HorizontalDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {


            OutlinedButton(
                onClick = { showDeleteTicketDialog = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) { Text("Cancel / Refund") }

            if (saleSelected.isEditable) {
                Button(
                    onClick = { onNavigateAction(saleId) },
                    modifier = Modifier
                        .sizeIn(maxWidth = 320.dp)
                ) { Icon(Icons.Default.Edit, "Add"); Text("Resume") }
            }
        }

    }
    if (showDeleteTicketDialog) {
        OnDeleteItemDialog(
            message = stringResource(id = R.string.delete_ticket),
            onDismissRequest = { showDeleteTicketDialog = false },
            onConfirmClicked = {
                ticketDetailsVM.onDeleteTicketAction()
                onNavigateAction(null)
            }
        )
    }


}


