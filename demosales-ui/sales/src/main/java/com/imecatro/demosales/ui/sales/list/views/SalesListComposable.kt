package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel
import com.imecatro.demosales.ui.theme.BlueTurquoise80

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SalesListComposable(
    list: List<SaleOnListUiModel> = fakelist,
    onCardClicked: (id : Long?) -> Unit = {},
    onAddNewSale: () -> Unit = {}
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { onAddNewSale() },
            containerColor = BlueTurquoise80,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
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
    SaleOnListUiModel(0, "Named", "30/01/2023", 20.0, "pending"),
    SaleOnListUiModel(1, "Someone", "30/01/2023", 20.0, "pending")
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