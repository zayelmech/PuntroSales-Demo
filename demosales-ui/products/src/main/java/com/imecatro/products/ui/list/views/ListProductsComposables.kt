package com.imecatro.products.ui.list.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.architect.isLoading
import com.imecatro.products.ui.R
import com.imecatro.products.ui.list.components.ProductCardCompose
import com.imecatro.products.ui.list.components.SearchProductTopBar
import com.imecatro.products.ui.list.model.CategoriesFilter
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.list.uistate.OrderedFilterState
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


private const val TAG = "ListProductsComposables"


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun ListOfProducts(
    list: List<ProductUiModel>,
    isLoading: Boolean = false,
    onSearchProduct: (String) -> Unit = {},
    searchList: List<ProductUiModel> = emptyList(),
    orderList: List<OrderedFilterState> = OrderedFilterState.filters,
    onCheckedChange: (OrderedFilterState) -> Unit = {},
    itemsSelectedQty: Int = 0, // The amount of items selected in the list
    showDownloadOptions: Boolean = false,
    onHideDownloadOptions: () -> Unit = {},
    onDownloadClicked: () -> Unit = {},
    onSelectAllChecked: (Boolean) -> Unit = {}, // for selection all item
    allSelected: Boolean = false,
    categories: List<CategoriesFilter> = emptyList(),
    onCategoryChecked: (CategoriesFilter) -> Unit = {},
    onEditCategoriesClicked: () -> Unit = {},
    onProductSelected: (Long?) -> Unit = {},
    onCardClicked: (Long?) -> Unit = {},
    onNavigateAction: () -> Unit = {},
) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    var showFilters by remember { mutableStateOf(false) }

    var orderedFilter by remember { mutableStateOf(false) }

    var categoriesFilter by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        // Add this line to fix the error:
        confirmValueChange = { true }, // Add a default confirmValueChange lambda
    )

    LaunchedEffect(text) {
        snapshotFlow { text }
            .debounce(300)
            .collect {
                onSearchProduct(it)
            }
    }

    Scaffold(
        floatingActionButton = {
            if (!expanded)
                FloatingActionButton(
                    onClick = { onNavigateAction() },
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
        },
        topBar = {
            val windowInsets = TopAppBarDefaults.windowInsets

            Column(modifier = Modifier.windowInsetsPadding(windowInsets)) {
                AnimatedVisibility(visible = !showDownloadOptions) {
                    Column {
                        SearchProductTopBar(
                            query = text,
                            onQueryChange = { text = it },
                            onSearchAction = { onSearchProduct(text) },
                            onClearSearchBar = { text = "" },
                            showFilters = showFilters,
                            onShowFiltersClicked = { showFilters = !showFilters }
                        )
                        AnimatedVisibility(visible = showFilters) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                FilterChip(
                                    onClick = { orderedFilter = true },
                                    label = {
                                        Text(stringResource(R.string.chip_order_by))
                                    },
                                    selected = orderList.any { it.isChecked },
                                    leadingIcon = {
                                        Icon(painterResource(R.drawable.filter_sorting), null)
                                    }
                                )

                                FilterChip(
                                    onClick = { categoriesFilter = true },
                                    label = {
                                        Text(stringResource(R.string.tittle_categories))
                                    },
                                    selected = categories.any { it.isChecked },
                                    leadingIcon = {
                                        Icon(Icons.Default.KeyboardArrowDown, null)
                                    }
                                )
                            }
                        }
                    }
                }
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
                                Icon(painterResource(R.drawable.file_earmark_richtext), "Download")
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
            }

        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLoading)
                items(10) { ShimmerListItem(Modifier.fillMaxWidth()) }
            else {
                items(if (text.isEmpty()) list else searchList) { product ->

                    ProductCardCompose(
                        product = product,
                        onLongClicked = { onProductSelected(product.id) },
                        onCardClicked = { onCardClicked(product.id) })
                    HorizontalDivider()
                }
            }
        }
    }

    if (orderedFilter)
        ModalBottomSheet(onDismissRequest = { orderedFilter = false }, sheetState = sheetState) {
            LazyColumn {
                items(orderList) { ordered ->
                    ListItem(
                        leadingContent = {
                            RadioButton(selected = ordered.isChecked, onClick = {
                                onCheckedChange(ordered)
                            })
                        },
                        headlineContent = {
                            Text(text = stringResource(ordered.text))
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }

    if (categoriesFilter)
        ModalBottomSheet(onDismissRequest = { categoriesFilter = false }, sheetState = sheetState) {
            LazyColumn {
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(text = stringResource(R.string.tittle_categories))
                        IconButton(onClick = {
                            categoriesFilter = false
                            onEditCategoriesClicked()
                        }) {
                            Icon(Icons.Default.Edit, null)
                        }
                    }
                }
                items(categories) { category ->
                    ListItem(
                        leadingContent = {
                            Checkbox(checked = category.isChecked, onCheckedChange = {
                                onCategoryChecked(category)
                            })
                        },
                        headlineContent = {
                            if (category.text.isEmpty())
                                Text(text = stringResource(R.string.txt_uncategorized))
                            else
                                Text(text = category.text)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
}


fun fakeProductsList(qty: Int): List<ProductUiModel> {
    val fakeList = mutableListOf<ProductUiModel>()
    for (i in 1L..qty) {
        fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", stock = "1", null, null))
    }
    return fakeList
}

/**
 * List of products state impl
 * This composable function is in charge of handling the UI events
 * observing states from the viewModel and remembering Coroutine Scope
 * in oder to make more lifecycle aware
 * @param productsViewModel this ViewModel of type ProductsViewModel is used hold the data and update the list once it's retrieve from the repository
 * @param onNavigateAction this lambda function allows you to navigate to another UI according to the value sent.<br> Example: onNavigate(1) will launch the Details screen of the product with the id = 1
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfProductsStateImpl(
    productsViewModel: ProductsViewModel,
    onCategoriesNav: () -> Unit,
    onCreateCatalog: () -> Unit = {},
    onNavigateAction: (Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val productsList by productsViewModel.productsList.collectAsState()
    val uiState by productsViewModel.uiState.collectAsState()

    val filters by productsViewModel.filtersState.collectAsState()

    val categories by productsViewModel.categories.collectAsState()

    val reportState by productsViewModel.reportState.collectAsState()

    var showShareReport by remember { mutableStateOf(false) }

    val showOptions = productsList.any { it.isSelected }
    ListOfProducts(
        list = productsList.toMutableStateList(),
        isLoading = uiState.isLoading,
        searchList = uiState.productsFiltered,
        onSearchProduct = { productsViewModel.onSearchAction(it) },
        orderList = filters,
        onCheckedChange = { productsViewModel.onFilterChange(it) },
        categories = categories,
        onCategoryChecked = { productsViewModel.onFilterCategory(it) },
        onEditCategoriesClicked = onCategoriesNav,
        onProductSelected = { productsViewModel.onProductSelected(it) },
        onCardClicked = {
            if (showOptions)
                productsViewModel.onProductSelected(it)
            else
                scope.launch { onNavigateAction(it) }
        },
        itemsSelectedQty = reportState.ids.size,
        showDownloadOptions = showOptions,
        onHideDownloadOptions = { productsViewModel.onClearSelections() },
        onDownloadClicked = { productsViewModel.onProcessProducts() },
        onSelectAllChecked = { productsViewModel.onSelectAllProducts(it) }, // for selection all item
        allSelected = reportState.allSelected,
        onNavigateAction = { onNavigateAction(null) })

    UiStateHandler(uiState) {

    }

    LaunchedEffect(reportState) {
        if (reportState.productsReady)
            showShareReport = true
    }

    if (showShareReport)
        ModalBottomSheet(onDismissRequest = {
            showShareReport = false
            productsViewModel.onCatalogShared()
        }
        ) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        onCreateCatalog()
                    }) {
                        Text(stringResource(R.string.txt_catalog_pdf))
                    }
                    Spacer(modifier = Modifier.weight(1f))
//                    IconButton(onClick = {
//                        reportState.salesFile?.share(context)
//                    }) { Icon(Icons.Default.Share, null) }
//
//                    IconButton(onClick = {
//                        reportState.salesFile?.download(context)
//                    }) { Icon(painterResource(com.imecatro.demosales.ui.sales.R.drawable.download), null) }
                }
            }
        }
}


@Preview(showBackground = true)
@Composable
fun PreviewListOfProducts() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {

            ListOfProducts(list = fakeProductsList(20), false) {
                Log.d(TAG, "PreviewListOfProducts: ")
            }
        }
    }
}

enum class ListProductsTestTags(val tag: String) {
    CARD("ElevatedCard")
}