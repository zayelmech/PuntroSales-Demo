package com.imecatro.demosales.ui.theme.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.ui.theme.common.CurrencyVisualTransformation
import com.imecatro.demosales.ui.theme.common.formatAsCurrency


enum class Type {
    Money, Decimal
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun InputNumberDialogComposable(
    initialValue: String = "",
    supportingMessage: String = "Write the new value",
    type: Type = Type.Decimal,
    extendedContent : @Composable ColumnScope.() -> Unit={},
    onDismissRequest: () -> Unit = {},
    onConfirmClicked: (String) -> Unit = {}
) {

    var qty by remember {
        mutableStateOf(initialValue)
    }

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
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
            Text(text = supportingMessage, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = qty,
                placeholder = { Text(if (type == Type.Money) "0.00".formatAsCurrency() else "0.0") },
                onValueChange = { qty = it.filter { c -> c.isDigit() || c == '.' } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                visualTransformation = if (type == Type.Money) CurrencyVisualTransformation() else VisualTransformation.None
            )
            extendedContent()
            Spacer(modifier = Modifier.size(20.dp))
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