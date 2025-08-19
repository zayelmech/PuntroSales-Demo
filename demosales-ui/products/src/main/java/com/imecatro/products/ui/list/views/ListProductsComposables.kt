package com.imecatro.products.ui.list.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.architect.UiStateHandler
import com.imecatro.demosales.ui.theme.architect.isLoading
import com.imecatro.products.ui.R
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
    onCardClicked: (Long?) -> Unit = {},
    onNavigateAction: () -> Unit = {},
) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(text) {
        snapshotFlow { text }
            .debounce(1000)
            .collect {
                onSearchProduct(it)
            }
    }

    Scaffold(floatingActionButton = {
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
                SearchBar(inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { onSearchProduct(text) },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Search a product") },
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

        }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
//                TextButton(onClick = { showFilters = true }) {
//                    Icon(Icons.Filled.ArrowDropDown, null)
//                    Text("Most popular")
//                }
//                if (!showFilters) {
//                    DropdownMenu(expanded = false, onDismissRequest = { showFilters = false }) {
//                        DropdownMenuItem(text = { Text("Most popular") }, onClick = {})
//                        DropdownMenuItem(text = { Text("Most popular..") }, onClick = {})
//                    }
//                }
//
//            }
            LazyColumn(
                modifier = Modifier.sizeIn(maxWidth = 411.dp),
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
            Text(text = "$${product.price}")
        }, trailingContent = {
            Text(text = "${product.stock}  ${product.unit}")
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
    onNavigateAction: (Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val productsList by productsViewModel.productsList.collectAsState()
    val uiState by productsViewModel.uiState.collectAsState()

    ListOfProducts(
        list = productsList.toMutableStateList(),
        isLoading = uiState.isLoading,
        searchList = uiState.productsFiltered,
        onSearchProduct = { productsViewModel.onSearchAction(it) },
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