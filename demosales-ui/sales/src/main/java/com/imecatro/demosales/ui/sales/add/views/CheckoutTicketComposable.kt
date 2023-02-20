package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.sales.add.model.ProductOnCartUiModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.theme.Typography

@Composable
fun CheckoutTicketComposable(
    list: List<ProductOnCartUiModel>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        //Cliente // search //guest
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Client", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            /*TODO implement client search tool*/
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "Guest")
            }
        }
        //Notas

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notes", style = Typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            /*TODO implement if in case user type a note*/
            TextButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Text(text = "Add note")
            }
        }

//        OutlinedTextField(
//            value = "",
//            onValueChange = {},
//            singleLine = false,
//            modifier = Modifier
//                .height(100.dp)
//                .fillMaxWidth()
//        )
        //Products
        Text(text = "Items", style = Typography.labelMedium)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                items(list) { product ->
                    Row {
                        Text(text = "${product.product.name}", modifier = Modifier.weight(3f))
                        Text(text = "x${product.qty}", modifier = Modifier.weight(1f))
                        Text(
                            text = "$${product.subtotal}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            //Envio cost
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Shipping")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "11")
                }
            }

            //Tax
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tax 16%")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "11")
                }
            }
            //Extra
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Extra")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "11")
                }
            }
            //Total
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "11")
                }
            }
        }
        Button(onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "Checkout", color = Color.White)
        }
        //State , save as
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCheckoutTicketComposable() {
    PuntroSalesDemoTheme {
        Surface {
            CheckoutTicketComposable(createFakeListOfProductsOnCart(50))
        }
    }
}