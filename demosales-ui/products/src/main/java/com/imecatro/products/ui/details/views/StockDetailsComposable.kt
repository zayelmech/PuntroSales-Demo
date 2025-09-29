package com.imecatro.products.ui.details.views

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.common.localDate
import com.imecatro.demosales.ui.theme.dialogs.InputNumberDialogComposable
import com.imecatro.products.ui.R
import com.imecatro.products.ui.details.model.ProductDetailsUiModel

@Preview(showBackground = true)
@Composable
fun StockComposable(
    stock: String = "1",
    cost: String = "212.0",
    list: List<ProductDetailsUiModel.History> = listOf(
        ProductDetailsUiModel.History(
            "da",
            "2",
            "2"
        ), ProductDetailsUiModel.History("01/02/2024", "-1", "Venta #123")
    ),
    onStockAdded: (String) -> Unit = {},
    onStockOut: (String) -> Unit = {}
) {

    var showDialogStockIn by remember { mutableStateOf(false) }
    var showDialogStockOut by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                item {
                    Column(
                        Modifier
                            .sizeIn(maxWidth = 360.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = stringResource(R.string.title_stock),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = stock,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = stringResource(R.string.title_cost),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = cost.formatAsCurrency(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Spacer(modifier = Modifier.size(30.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                modifier = Modifier.weight(1f),

                                onClick = { showDialogStockOut = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon( painter = painterResource(R.drawable.baseline_remove_24), null)
                                Text(stringResource(R.string.btn_stock_out))
                            }

                            Button(
                                modifier = Modifier.weight(1f),

                                onClick = { showDialogStockIn = true }) {
                                Icon(Icons.Filled.Add, null)
                                Text(stringResource(R.string.btn_stock_in))
                            }

                        }

                        Spacer(modifier = Modifier.size(20.dp))

                        HorizontalDivider()
                    }

                }
                item {
                    if (list.isEmpty())
                        Text(
                            text = stringResource(R.string.desc_no_items),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    else
                        Text(
                            text = stringResource(R.string.title_history),
                            style = MaterialTheme.typography.titleLarge
                        )
                }
                items(list) { item ->
                    ListItem(
                        headlineContent = { Text(item.tittle) },
                        trailingContent = {
                            Text(
                                item.qty,
                                color = if (item.qty.contains('-')) Color.Red else Color(0xff37c8ab)
                            )
                        },
                        supportingContent = { Text(item.date.localDate()) }
                    )
                    HorizontalDivider()
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }

    if (showDialogStockIn)
        InputNumberDialogComposable(
            initialValue = "",
            supportingMessage = stringResource(R.string.message_register_add_stock),
            onDismissRequest = { showDialogStockIn = false },
            onConfirmClicked = {
                onStockAdded(it)
                showDialogStockIn = false
            }
        )

    if (showDialogStockOut)
        InputNumberDialogComposable(
            initialValue = "",
            supportingMessage = stringResource(R.string.message_register_stock_out),
            onDismissRequest = { showDialogStockOut = false },
            onConfirmClicked = {
                onStockOut(it)
                showDialogStockOut = false
            }
        )
}