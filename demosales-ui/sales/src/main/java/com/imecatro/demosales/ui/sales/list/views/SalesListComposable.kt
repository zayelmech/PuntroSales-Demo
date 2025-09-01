package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.model.StatusFilterUiModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SalesListComposable(
    list: List<SaleOnListUiModel> = fakelist,
    statusList: List<StatusFilterUiModel> = emptyList(),
    onCheckedChange: (StatusFilterUiModel) -> Unit = {},
    onCardClicked: (id: Long?) -> Unit = {},
    onAddNewSale: () -> Unit = {}
) {

    val scrollState = rememberLazyListState()

    var dateFilter by remember { mutableStateOf(false) }

    var statusFilter by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton = {


        FloatingActionButton(
            onClick = { onAddNewSale() },
        ) {
            if (scrollState.isScrollInProgress)
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            else {
                Row(Modifier.padding(10.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
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
            item {

                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
                    Spacer(modifier = Modifier.size(20.dp))

                    FilterChip(
                        onClick = { statusFilter = true },
                        label = {
                            Text(stringResource(R.string.txt_filter_state))
                        },
                        selected = statusList.any { it.isChecked },
                        leadingIcon = {
                            if (statusList.any { it.isChecked }) {
                                Icon(Icons.Filled.Done, null)
                            } else {
                                Icon(Icons.Filled.ArrowDropDown, null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.size(20.dp))
//                    OutlinedButton(
//                        onClick = { dateFilter = true },
//                        shape = MaterialTheme.shapes.medium
//                    ) {
//                        Icon(Icons.Filled.ArrowDropDown, null)
//                        Text(stringResource(R.string.txt_filter_date))
//                    }
                }
            }
            items(list) { sale ->
                CardOfSaleComposable(sale = sale) { onCardClicked(sale.id) }
            }
        }

        if (dateFilter)
            ModalBottomSheet(onDismissRequest = { dateFilter = false }) {
                Text(text = "Any time")
                Text(text = "One week ago")
                ListItem(leadingContent = {
                    RadioButton(selected = true, onClick = {})
                }, headlineContent = {
                    Text(text = "One month ago")
                })
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Custom Range")
                }
            }

        if (statusFilter)
            ModalBottomSheet(onDismissRequest = { statusFilter = false }) {
                LazyColumn {
                    items(statusList) { status ->
                        ListItem(
                            leadingContent = {
                                Checkbox(
                                    checked = status.isChecked,
                                    onCheckedChange = { onCheckedChange(status) })
                            },
                            headlineContent = {
                                Text(text = status.text)
                            }
                        )
                    }
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

    val statusFilterUiState by salesListViewModel.statusFilterState.collectAsState()

    SalesListComposable(
        list = listUiState,
        onCardClicked = onNavigate,
        onCheckedChange = { salesListViewModel.onStatusFilterChange(it) },
        statusList = statusFilterUiState,
        onAddNewSale = { onNavigate(null) })

}