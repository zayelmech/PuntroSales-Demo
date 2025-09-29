package com.imecatro.products.ui.list.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.architect.isLoading
import com.imecatro.demosales.ui.theme.common.formatAsCurrency
import com.imecatro.products.ui.R
import com.imecatro.products.ui.list.model.CategoriesFilter
import com.imecatro.products.ui.list.model.OrderedFilterUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
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
    orderList: List<OrderedFilterUiModel> = OrderedFilterUiModel.filters,
    onCheckedChange: (OrderedFilterUiModel) -> Unit = {},
    categories: List<CategoriesFilter> = emptyList(),
    onCategoryChecked: (CategoriesFilter) -> Unit = {},
    onEditCategoriesClicked: () -> Unit = {},
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = text,
                            onQueryChange = { text = it },
                            onSearch = { onSearchProduct(text) },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            placeholder = { Text(stringResource(R.string.search_placeholder)) },
                            leadingIcon = {
                                if (expanded) {
                                    IconButton(onClick = {
                                        expanded = false
                                    }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                            },
                            trailingIcon = {
                                if (!expanded)
                                    IconButton(
                                        onClick = {
                                            showFilters = !showFilters
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = if (showFilters) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
                                    ) {
                                        Icon(painterResource(R.drawable.sliders), null)
                                    }
                            }

                        )
                    }, expanded = expanded, onExpandedChange = { expanded = it },
                    content = {
                        LazyColumn(
                            modifier = Modifier.sizeIn(maxWidth = 411.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(searchList) { product ->
                                ProductCardCompose(product = product) { onCardClicked(product.id) }
                                HorizontalDivider()
                            }
                        }
                    })
            }

        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
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
                            Text( stringResource(R.string.chip_order_by))
                        },
                        selected = orderList.any { it.isChecked },
                        leadingIcon = {
                            Icon(painterResource(R.drawable.filter_sorting), null)
                        }
                    )

                    FilterChip(
                        onClick = { categoriesFilter = true },
                        label = {
                            Text( stringResource(R.string.tittle_categories))
                        },
                        selected = categories.any { it.isChecked },
                        leadingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isLoading) {

                    items(10) {
                        ShimmerListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                } else {
                    items(list) { product ->

                        ProductCardCompose(product = product) { onCardClicked(product.id) }
                        HorizontalDivider()
                    }
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
                        }
                    )
                }
            }
        }

    if (categoriesFilter)
        ModalBottomSheet(onDismissRequest = { categoriesFilter = false }, sheetState = sheetState) {
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(R.string.tittle_categories))
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                categoriesFilter = false
                                onEditCategoriesClicked()
                            }) {
                                Icon(Icons.Default.Edit, null)
                            }
                        }
                    )
                }
                items(categories) { category ->
                    ListItem(
                        leadingContent = {
                            Checkbox(checked = category.isChecked, onCheckedChange = {
                                onCategoryChecked(category)
                            })
                        },
                        headlineContent = {
                            Text(text = category.text)
                        }
                    )
                }
            }
        }
}

@Composable
fun ProductCardCompose(product: ProductUiModel, onCardClicked: () -> Unit) {

    val cardTag = "${ListProductsTestTags.CARD.tag}-${product.id}"


    Box(
        modifier = Modifier
            .clickable { onCardClicked() }
            .testTag(cardTag),
    ) {
        ListItem(leadingContent = {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .error(R.drawable.baseline_insert_photo_24)
                        .crossfade(true)
                        .build()

                ),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(25)),
                contentScale = ContentScale.FillWidth
            )
        }, headlineContent = {
            Text(text = product.name ?: "Product name")
        }, supportingContent = {
            Text(text = "${product.price?.formatAsCurrency()}")
        }, trailingContent = {
            val color =
                if (product.stock.contains("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            Text(text = "${product.stock}  ${product.unit}", color = color)
        })
    }

}

fun fakeProductsList(qty: Int): List<ProductUiModel> {
    val fakeList = mutableListOf<ProductUiModel>()
    for (i in 1L..qty) {
        fakeList.add(ProductUiModel(i, "Product Name $i", "3.00", "pz", stock = "1", null))
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
@Composable
fun ListOfProductsStateImpl(
    productsViewModel: ProductsViewModel,
    onCategoriesNav: () -> Unit,
    onNavigateAction: (Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val productsList by productsViewModel.productsList.collectAsState()
    val uiState by productsViewModel.uiState.collectAsState()

    val filters by productsViewModel.filtersState.collectAsState()

    val categories by productsViewModel.categories.collectAsState()


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
        onCardClicked = {
            scope.launch { onNavigateAction(it) }
        },
        onNavigateAction = { onNavigateAction(null) })

    UiStateHandler(uiState) {

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