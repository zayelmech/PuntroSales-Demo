package com.imecatro.demosales.ui.sales.add.screens

import android.app.Activity
import android.content.ClipData
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.BitmapComposer
import com.imecatro.demosales.ui.sales.details.viewmodel.TicketDetailsViewModel
import com.imecatro.demosales.ui.sales.details.views.ResumeTicketScreen
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import kotlinx.coroutines.launch


// -----------------------
// Route + Share (full height capture)
// -----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeTicketScreenImpl(
    ticketDetailsVM: TicketDetailsViewModel,
    saleId: Long,
    onNavigateAction: (Long?) -> Unit
) {

    val ticket by ticketDetailsVM.sale.collectAsState()

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

                                    ResumeTicketScreen(ticket)
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
        TopAppBar(
            title = { Text(text = "Sale Details") },
            actions = {
                IconButton(onClick = { onShareTicket.invoke() }) {
                    Icon(Icons.Default.Share, contentDescription = "Share ticket")
                }
            }
        )
        HorizontalDivider()
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResumeTicketScreen(ticketDetails = ticket)
        }
        HorizontalDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {


            OutlinedButton(
                onClick = { onNavigateAction(null) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) { Text("Back to sales") }

            Button(
                onClick = { onNavigateAction(saleId) },
                modifier = Modifier
                    .sizeIn(maxWidth = 320.dp)
            ) { Icon(Icons.Default.Add, "Add"); Text("New Sale") }
        }

    }

}