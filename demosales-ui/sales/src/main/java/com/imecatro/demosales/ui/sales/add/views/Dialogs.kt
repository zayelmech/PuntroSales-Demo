package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDeleteItemDialog(
    productName: String,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest, modifier = Modifier
            .background(
                Color.White,
                RoundedCornerShape(10)
            )
            .padding(10.dp)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)

            Text(text = "Do you want to remove $productName from list", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = { onConfirmClicked() }) {
                    Text(text = "Remove")
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewOnDeleteItemDialog() {
    PuntroSalesDemoTheme {
        Surface {
            OnDeleteItemDialog("PRODUCT", {}, {})
        }
    }
}