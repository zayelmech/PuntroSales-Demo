package com.imecatro.ui.clients.addclient.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddClientComposable(
    onSaveButtonClicked: () -> Unit
) {
    LazyColumn {
        item {
            Column (modifier = Modifier.padding(10.dp)) {

            }
        }
    }
}