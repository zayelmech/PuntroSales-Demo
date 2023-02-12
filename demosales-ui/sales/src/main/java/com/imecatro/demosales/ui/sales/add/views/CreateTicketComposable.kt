package com.imecatro.demosales.ui.sales.add.views

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.imecatro.demosales.ui.sales.R
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.sales.add.model.ProductResultUiModel
import com.imecatro.demosales.ui.sales.add.uistate.TicketUiState
import com.imecatro.demosales.ui.sales.add.viewmodel.AddSaleViewModel
import com.imecatro.demosales.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketComposable(
    productsOnCart: List<ProductOnCartUiModel>,
    onDeleteProduct: (Int) -> Unit,
    onProductPlusClicked: (Int) -> Unit,
    onProductMinusClicked: (Int) -> Unit,
    onQtyValueChange: (Int, Float) -> Unit,
    onAddProductClicked: () -> Unit
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = onAddProductClicked,
            containerColor = BlueTurquoise80,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {

                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    enabled = productsOnCart.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        BlueTurquoise40),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(text = "SAVE", color = Color.White)
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = { /*TODO*/ },
                    enabled = productsOnCart.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        BlueTurquoise40),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20)
                ) {
                    Text(text = "CHARGE $${0.0}",color = Color.White)
                }
            }

            Divider(color = Color.LightGray, thickness = 1.dp)

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
//            modifier = Modifier.consumeWindowInsets(innerPadding),
//            contentPadding = innerPadding
            ) {
                if (productsOnCart.isEmpty()) {
                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), verticalAlignment = Alignment.CenterVertically) {

                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_playlist_add_24),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { onAddProductClicked() },
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                itemsIndexed(productsOnCart) { index, item ->
                    OrderOnCartComposable(
                        product = item,
                        productPosition = index,
                        onPlusClicked = onProductPlusClicked,
                        onMinusClick = onProductMinusClicked,
                        onQtyValueChange = { onQtyValueChange(index, it.toFloat()) },
                    )
                    //TODO implement onDelete
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderOnCartComposable(
    product: ProductOnCartUiModel,
    productPosition: Int,
    onPlusClicked: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,
    onQtyValueChange: (String) -> Unit,
) {

    val cardTag = "CARD-${product.product.id}"

    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth()
//            .height(120.dp)
//        .clickable { onCardClicked() }
            .testTag(cardTag),
        elevation = CardDefaults.cardElevation(0.5.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // TODO add description and implement image by url

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(product.product.imageUri)
                        .error(R.drawable.baseline_insert_photo_24)
                        .crossfade(true)
                        .build()

                ),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
//                    .padding(5.dp)
                    .clip(RoundedCornerShape(50)),
                contentScale = ContentScale.FillBounds
            )

            Column(Modifier.weight(1f)) {
                Text(text = product.product.name ?: "Product name", style = Typography.titleMedium)
//                Text(text = " x ${product.product.unit}", fontSize = 18.sp)
                Text(text = "$${product.product.price ?: 0.00}", style = Typography.titleMedium)

            }
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .padding(5.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                OutlinedTextField(
                    value = "${product.qty}",
                    onValueChange = onQtyValueChange,
                    modifier = Modifier.padding(0.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(Modifier.padding(5.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onMinusClick(productPosition) }
                            .background(GreenTurquoise, RoundedCornerShape(50)),
                        tint = Color.White

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Click to add 1 ",
                        modifier = Modifier
                            .clickable { onPlusClicked(productPosition) }
                            .background(GreenTurquoise, RoundedCornerShape(50)),
                        tint = Color.White
                    )


                }
            }
            Text(text = "$${product.subtotal}", style = Typography.titleMedium)

        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateTicketComposableStateImpl(
    addSaleViewModel: AddSaleViewModel
) {
    val resultsList by addSaleViewModel.productsFound.collectAsState()
    val productsOnCart by addSaleViewModel.cartList.collectAsState()

    var query by remember {
        mutableStateOf("")
    }
//    var qtyValue by remember {
//        mutableStateOf(0f)
//    }
    var subTotalProduct by remember {
        mutableStateOf(0f)
    }

    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            SearchBottomSheetComposable(
                list = resultsList.toMutableStateList(),
                query = query,
                onQueryChange = {
                    query = it
                    addSaleViewModel.onSearchAction(query)
                },
                onProductClicked = {
                    addSaleViewModel.onAddProductToCartAction(it)
                    scope.launch { state.hide() }
                    //TODO hide keyboard
                }
            )
        }
    ) {
        CreateTicketComposable(
            productsOnCart = productsOnCart.toMutableStateList(),
            onDeleteProduct = {/*TODO */ },
            onProductMinusClicked = {
                addSaleViewModel.onQtyValueIncreaseAtPos(it, -1)
            },
            onProductPlusClicked = {
                addSaleViewModel.onQtyValueIncreaseAtPos(it, 1)
            },
            onQtyValueChange = { pos, number ->
                addSaleViewModel.onQtyValueChangeAtPos(pos, number)
            }
        ) {
            scope.launch { state.show() }
        }
    }
    val ticketState by addSaleViewModel.ticketState.collectAsState()
    when (ticketState) {
        is TicketUiState.Initialized -> {
            addSaleViewModel.onGetCacheTicketAction()
        }
        is TicketUiState.OnCache -> {

            Log.d(
                "TAG",
                "CreateTicketComposableStateImpl: ${(ticketState as TicketUiState.OnCache).cart.size}"
            )
        }
        is TicketUiState.Saved -> {}
        is TicketUiState.Error -> {}
    }

}

fun createFakeListOfProductsOnCart(num: Int): List<ProductOnCartUiModel> {
    val list: MutableList<ProductOnCartUiModel> = mutableListOf()
    for (i in 1..num) {
        val new = ProductResultUiModel(
            id = i,
            name = "Product name $i",
            price = 1f,
            imageUri = null
        )
        val new2 = ProductOnCartUiModel(new, 0f, 0f)

        list.add(new2)
    }
    return list
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateTicketComposable() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTicketComposable(
                createFakeListOfProductsOnCart(5),
                {},
                {},
                {},
                { _, _ -> }
            ) {}

        }
    }
}

