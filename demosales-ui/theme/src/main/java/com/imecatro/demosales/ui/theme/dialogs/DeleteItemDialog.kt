package com.imecatro.demosales.ui.theme.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


enum class DialogType {

    Delete, Warning, Error, Info
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ActionDialog(
    dialogType: DialogType = DialogType.Delete,
    icon: @Composable (() -> Unit)? = null,
    title: String = "Are you sure?",
    message: String = "Would you like to delete this item?",
    onDismissRequest: () -> Unit = {},
    onConfirmClicked: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(20.dp))
                val iconVector = when (dialogType) {
                    DialogType.Delete -> Icons.Outlined.Delete
                    DialogType.Warning -> Icons.Outlined.Warning
                    DialogType.Error -> Icons.Outlined.Clear
                    DialogType.Info -> Icons.Outlined.Info
                }
                if (icon != null)
                    icon()
                else
                    Icon(imageVector = iconVector, contentDescription = null)

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "Cancel")
                    }
                    TextButton(onClick = { onConfirmClicked() }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}
