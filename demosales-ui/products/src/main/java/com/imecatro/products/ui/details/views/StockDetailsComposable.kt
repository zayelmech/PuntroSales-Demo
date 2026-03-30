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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier,
    stock: String = "1",
    cost: String = "212.0",
    list: List<ProductDetailsUiModel.History> = listOf(
        ProductDetailsUiModel.History(
            "2024-02-01T12:00:00Z",
            "2",
            "Initial stock"
        ), ProductDetailsUiModel.History("2024-02-01T14:00:00Z", "-1", "Venta #123")
    ),
    onStockAdded: (String) -> Unit = {},
    onStockOut: (String) -> Unit = {}
) {

    var showDialogStockIn by remember { mutableStateOf(false) }
    var showDialogStockOut by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.size(16.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxWidth = 400.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.title_stock),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stock,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.title_cost),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = cost.formatAsCurrency(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.size(8.dp))
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
                        Icon(
                            painter = painterResource(R.drawable.baseline_remove_24),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.btn_stock_out))
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { showDialogStockIn = true }
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.btn_stock_in))
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            item {
                if (list.isEmpty()) {
                    Text(
                        text = stringResource(R.string.desc_no_items),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.title_history),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(list) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            item.tittle,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    trailingContent = {
                        val isNegative = item.qty.contains('-')
                        Text(
                            text = if (isNegative) item.qty else "+${item.qty}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isNegative) MaterialTheme.colorScheme.error else Color(0xff37c8ab)
                        )
                    },
                    supportingContent = {
                        Text(
                            item.date.localDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
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
