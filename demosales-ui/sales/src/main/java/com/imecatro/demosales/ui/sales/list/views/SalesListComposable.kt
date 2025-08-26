package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel

@Preview(showBackground = true)
@Composable
fun SalesListComposable(
    list: List<SaleOnListUiModel> = fakelist,
    onCardClicked: (id: Long?) -> Unit = {},
    onAddNewSale: () -> Unit = {}
) {

    val scrollState = rememberLazyListState()
    Scaffold(floatingActionButton = {


        FloatingActionButton(
            onClick = { onAddNewSale() },
        ) {
            if (scrollState.isScrollInProgress)
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            else {
                Row(Modifier.padding(10.dp,0.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.size(5.dp))
                    Text("New Sale")
                }
            }
        }

    }) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = innerPadding
        ) {
            items(list) { sale ->
                CardOfSaleComposable(sale = sale) { onCardClicked(sale.id) }
            }
        }
    }
}


private val fakelist = listOf(
    SaleOnListUiModel(0, "Named", "30/01/2023", 20.0, "pending", Color.Red),
    SaleOnListUiModel(1, "Someone", "30/01/2023", 20.0, "pending", Color.Gray)
)

@Composable
fun SalesListComposableStateImpl(
    salesListViewModel: SalesListViewModel,
    onNavigate: (Long?) -> Unit
) {

    val listUiState by salesListViewModel.salesListUiState.collectAsState()

    SalesListComposable(
        list = listUiState,
        onCardClicked = onNavigate,
        onAddNewSale = { onNavigate(null) })

}