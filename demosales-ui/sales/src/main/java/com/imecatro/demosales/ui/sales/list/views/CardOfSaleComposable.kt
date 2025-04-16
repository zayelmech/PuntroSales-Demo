package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel

@Preview(showBackground = true)
@Composable
fun CardOfSaleComposable(
    sale: SaleOnListUiModel = SaleOnListUiModel(0, "Ab", date = "01/02/2022", 200.0, "f"),
    onCardClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clickable { onCardClicked() },
    ) {
        ListItem(
            modifier = Modifier.sizeIn(maxWidth = 411.dp),
            headlineContent = {
                Text(text = "$${sale.total}")
            },
            overlineContent = {
                Text(text = sale.clientName)
            },
            supportingContent = {
                Text(text = sale.date)
            },
            trailingContent = {
                Text(text = sale.status)
            }
        )
    }
}