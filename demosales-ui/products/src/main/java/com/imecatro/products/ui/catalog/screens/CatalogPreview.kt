package com.imecatro.products.ui.catalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imecatro.products.ui.R
import com.imecatro.products.ui.catalog.components.SavePdfButton
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogPreview(productsViewModel: ProductsViewModel, onBack: () -> Unit) {

    val productsList by productsViewModel.reportState.collectAsState()

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.top_bar_catalog)) },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        null
                    )
                }
            }, actions = {
//                IconButton(onClick = { onShareTicket.invoke() }) {
//                    Icon(Icons.Default.Share, contentDescription = "Share ticket")
//                }
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            HtmlWebView(productsList.getHtml(context), modifier = Modifier.fillMaxSize())
        }

        Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.Center) {
            SavePdfButton(html = productsList.getHtml(context))
        }
    }
}