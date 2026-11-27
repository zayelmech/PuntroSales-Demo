package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.theme.LocalCurrencyCode
import com.imecatro.demosales.ui.theme.common.Money
import com.imecatro.demosales.ui.theme.common.localDate
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun CardOfSaleComposable(
    sale: SaleOnListUiModel = SaleOnListUiModel(
        id = 0,
        clientName = "Ab",
        date = "01/02/2022",
        total = 200.0,
        status = "Completed",
        statusColor = Color.Blue,
        isSelected = true
    ),
    locale: Locale = LocalLocale.current.platformLocale,
    currencyCode: String? = LocalCurrencyCode.current,
    onLongClicked: () -> Unit = {},
    onCardClicked: () -> Unit = {}
) {
    val formattedTotal = remember(sale.total, locale, currencyCode) {
        runCatching { Money.format(sale.total.toString(), locale, currencyCode) }
            .getOrDefault("0.0")
    }
    val backgroundColor = if (sale.isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .background(backgroundColor)
            .combinedClickable(
                onClick = onCardClicked,
                onLongClick = onLongClicked
            )
    ) {
        ListItem(
            leadingContent = {
                if (sale.isSelected) {
                    Column(
                        modifier = Modifier.sizeIn(minHeight = 60.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, "Selected",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            },
            headlineContent = {
                Text(text = formattedTotal)
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
                        .padding(vertical = 15.dp, horizontal = 0.dp)
                        .background(color = sale.statusColor, shape = RoundedCornerShape(5.dp))
                        .sizeIn(minWidth = 70.dp, minHeight = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sale.status)
                }


            },
            // Make ListItem background transparent to see the Box color
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)

        )
    }
}
