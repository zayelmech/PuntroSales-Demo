package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.demosales.ui.theme.common.localDate

@Preview(showBackground = true)
@Composable
fun CardOfSaleComposable(
    sale: SaleOnListUiModel = SaleOnListUiModel(
        0,
        "Ab",
        date = "01/02/2022",
        200.0,
        "Completed",
        Color.Blue
    ),
    onCardClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clickable { onCardClicked() },
    ) {
        ListItem(
            headlineContent = {
                Text(text = sale.total.formatAsCurrency())
            },
            overlineContent = {
                Text(text = sale.clientName)
            },
            supportingContent = {
                Text(text = "#${sale.id}  - ${sale.date.localDate()}")
            },
            trailingContent = {
                Box(
                    modifier = Modifier
                        .background(color = sale.statusColor, shape = RoundedCornerShape(5.dp))
                        .padding(10.dp)
                        .sizeIn(minWidth = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sale.status)
                }


            }
        )
    }
}