package com.imecatro.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.products.views.ProductCardCompose
import com.imecatro.ui.theme.PuntroSalesDemoTheme


@Preview(showBackground = true)
@Composable
fun AtomsPreview() {
    PuntroSalesDemoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProductCardCompose(ProductUiModel(1,"Rice","2.00","1kg",""),{})
        }
    }
}
