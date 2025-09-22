package com.imecatro.demosales.ui.theme.architect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


/**
 * @param state
 * @param onDismiss do something when dialog is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiStateHandler(state: UiState, onDismiss: () -> Unit) {

    if (state.isLoading && state.error == null) Dialog(onDismissRequest = {}) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    if (state.error != null) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
        ) {
            Surface(
                //color = Color.White,
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = RoundedCornerShape(30.dp),
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(20.dp))
                    if (state.error!!.tittle.isNotBlank()) {
                        Text(
                            text = state.error!!.tittle,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    Text(
                        text = state.error?.message ?: "Error 404",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.sizeIn(minWidth = 200.dp, minHeight = 45.dp),
                    ) {
                        val textStyle = TextStyle(fontSize = 16.sp)
                        Text("Aceptar", style = textStyle)
                    }
                }
            }
        }
    }

}
