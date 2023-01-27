package com.imecatro.products.ui.list.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.imecatro.products.ui.R
import com.imecatro.products.ui.common.ButtonFancy
import com.imecatro.products.ui.details.model.ProductDetailsUiModel
import com.imecatro.products.ui.list.model.ProductUiModel
import com.imecatro.products.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.theme.PurpleGrey40
import com.imecatro.products.ui.theme.PurpleRed
import com.imecatro.products.ui.theme.Typography
import com.imecatro.products.ui.theme.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetDetailsCompose(
    productDetails: ProductDetailsUiModel?,
    state: ModalBottomSheetState,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit,
    content: @Composable () -> Unit
) {

    ModalBottomSheetLayout(sheetState = state, sheetContent = {
        DetailsComposable(productDetails, onDeleteClicked, onEditClicked)
    }, sheetShape = RoundedCornerShape(20.dp, 20.dp)) {
        content()
    }
}

@Composable
fun DetailsComposable(
    productDetails: ProductDetailsUiModel?,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    val paddingX = 20.dp
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = rememberAsyncImagePainter(productDetails?.imageUrl ?: R.raw.arcreactor),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .requiredSizeIn(maxHeight = 280.dp)
//                .padding(paddingX)
//                .clip(RoundedCornerShape(10))
            ,
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            //tittle
            Text(text = productDetails?.name ?: "No name", style = Typography.titleMedium)
            //pz
            Text(text = productDetails?.unit ?: "", style = Typography.bodyLarge)
            //price
            Text(text = productDetails?.price ?: "$0.00", style = Typography.titleMedium)
            Spacer(modifier = Modifier.height(20.dp))
            //Details
            Text(text = "Details", style = Typography.labelMedium, color = PurpleGrey40)
            Divider(color = Color.LightGray, thickness = 2.dp)
            Text(
                text =  productDetails?.details?:"This text must contains some details about this product",
                style = Typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
        //button edit
        ButtonFancy(
            text = "EDIT",
            paddingX = paddingX,
            icon = Icons.Filled.Edit,
            onClicked = onEditClicked
        )
        //button delete
        ButtonFancy(
            text = "DELETE",
            color = PurpleRed,
            paddingX = paddingX,
            icon = Icons.Filled.Delete,
            onClicked = onDeleteClicked
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWordsListDetailsCompose() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DetailsComposable(
                productDetails = ProductDetailsUiModel(1, "Cebolla", "$0.00", "x pz", null,"details"),
                onDeleteClicked = { /*TODO*/ }) {

            }
        }
    }
}