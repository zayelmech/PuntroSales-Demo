package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.list.model.SaleOnListUiModel
import com.imecatro.demosales.ui.sales.list.model.StatusFilterUiModel
import com.imecatro.demosales.ui.sales.list.viewmodel.SalesListViewModel
import com.imecatro.demosales.ui.theme.common.download
import com.imecatro.demosales.ui.theme.common.open
import com.imecatro.demosales.ui.theme.common.share
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SalesListComposable(
    list: List<SaleOnListUiModel> = fakelist,
    statusList: List<StatusFilterUiModel> = emptyList(),
    onStatusFilterChecked: (StatusFilterUiModel) -> Unit = {},
    itemsSelectedQty: Int = 0, // The amount of items selected in the list
    showDownloadOptions: Boolean = false,
    onHideDownloadOptions: () -> Unit = {},
    onDownloadClicked: () -> Unit = {},
    onSelectAllChecked: (Boolean) -> Unit = {}, // for selection all items
    allSelected: Boolean = false,
    onCardSelected: (id: Long) -> Unit = {},
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
                    Text(stringResource(R.string.btn_new_sale))
                }
            }
        }

    }, topBar = {
        val windowInsets = TopAppBarDefaults.windowInsets

        Column(modifier = Modifier.windowInsetsPadding(windowInsets)) {
            AnimatedVisibility(visible = showDownloadOptions) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onHideDownloadOptions) {
                            Icon(Icons.Default.Close, null)
                        }
                        Text("$itemsSelectedQty")
                        Spacer(Modifier.weight(1f))

                        FilledIconButton(onClick = onDownloadClicked) {
                            Icon(painterResource(R.drawable.filetype_csv), "Download")
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    Row(
                        Modifier.wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(allSelected, onCheckedChange = { onSelectAllChecked(it) })
                        Text(stringResource(R.string.txt_select_all))
                    }
                }
            }
            AnimatedVisibility(!showDownloadOptions) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
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
                }
            }

        }

    }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(list) { sale ->
                    CardOfSaleComposable(
                        sale = sale,
                        onLongClicked = { onCardSelected(sale.id) },
                        onCardClicked = { onCardClicked(sale.id) }
                    )
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
                                        onCheckedChange = { onStatusFilterChecked(status) })
                                },
                                headlineContent = {
                                    Text(text = status.text)
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
        }
    }
}


private val fakelist = listOf(
    SaleOnListUiModel(0, "Named", "30/01/2023", 20.0, "pending", Color.Red),
    SaleOnListUiModel(1, "Someone", "30/01/2023", 20.0, "pending", Color.Gray)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesListComposableStateImpl(
    salesListViewModel: SalesListViewModel,
    onNavigate: (Long?) -> Unit
) {

    //List
    val listUiState by salesListViewModel.salesListUiState.collectAsState()
    // Filters
    val statusFilterUiState by salesListViewModel.statusFilterState.collectAsState()
    // Reports
    val reportState by salesListViewModel.reportState.collectAsState()

    val showOptions = listUiState.any { it.isSelected }

    var showReports by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    SalesListComposable(
        list = listUiState,
        onCardClicked = { id ->
            if (showOptions)
                salesListViewModel.onCardSelected(id ?: 0L)
            else
                onNavigate(id)
        },
        itemsSelectedQty = reportState.ids.size,
        onHideDownloadOptions = { salesListViewModel.onClearSelections() },
        showDownloadOptions = showOptions,
        onDownloadClicked = { salesListViewModel.onDownloadCsv() },
        onSelectAllChecked = { salesListViewModel.onSelectAllSales(it) },
        allSelected = reportState.allSelected,
        onCardSelected = { salesListViewModel.onCardSelected(it) },
        onStatusFilterChecked = { salesListViewModel.onStatusFilterChange(it) },
        statusList = statusFilterUiState,
        onAddNewSale = { onNavigate(null) })

    LaunchedEffect(reportState) {
        if (reportState.salesFile != null) {
            scope.launch { showReports = true }
        }
    }

    val context = LocalContext.current

    if (showReports)
        ModalBottomSheet(onDismissRequest = {
            salesListViewModel.onReportSent()
            showReports = false
        }) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        reportState.salesFile?.open(context)
                    }) {
                        Text(stringResource(R.string.txt_sales_report))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        reportState.salesFile?.share(context)
                    }) { Icon(Icons.Default.Share, null) }

                    IconButton(onClick = {
                        reportState.salesFile?.download(context)
                    }) { Icon(painterResource(R.drawable.download), null) }

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        reportState.groupedProductsFile?.open(context)
                    }) {
                        Text(stringResource(R.string.txt_products_report))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        reportState.groupedProductsFile?.share(context)
                    }) { Icon(Icons.Default.Share, null) }
                    IconButton(onClick = {
                        reportState.groupedProductsFile?.download(context)
                    }) { Icon(painterResource(R.drawable.download), null) }

                }
            }
        }
}