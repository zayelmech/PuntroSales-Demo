package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun CardOfSaleComposable(
    sale: SaleOnListUiModel = SaleOnListUiModel(0, "Ab", date = "01/02/2022", 200.0, "f"),
    onCardClicked: () -> Unit = {}
) {

    //val cardTag = "${ListProductsTestTags.CARD.tag}-${product.id}"

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth(0.9f)
//            .width(350.dp)
            .clickable { onCardClicked() },
        //.testTag(cardTag),
        elevation = CardDefaults.cardElevation(0.5.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sale.clientName ?: "", style = Typography.titleMedium)
                Text(text = sale.date, fontSize = 18.sp)
                Text(text = sale.status)
            }
            Text(text = "$${sale.total ?: 0.00}", style = Typography.titleMedium)
        }
    }
}