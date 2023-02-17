package com.imecatro.demosales.ui.sales.add.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumberDialogComposable(
    initialValue: String,
    onDismissRequest: () -> Unit,
    onConfirmClicked: (String) -> Unit
) {

    var qty by remember {
        mutableStateOf(initialValue)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest, modifier = Modifier
            .background(
                Color.White,
                RoundedCornerShape(10)
            )
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
            Text(text = "Write the new value", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = qty,
                onValueChange = { qty = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = { onConfirmClicked(qty) }) {
                    Text(text = "Confirm")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInputNumberDialogComposable() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            InputNumberDialogComposable(
                initialValue = "1.0",
                onDismissRequest = {},
                onConfirmClicked = {}
            )
        }
    }
}
