package com.imecatro.demosales

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.ui.add.viewmodel.AddViewModel
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.products.viewmodels.ProductsViewModel
import com.imecatro.ui.products.views.BottomSheetDetailsCompose
import com.imecatro.ui.products.views.ListOfProducts
import com.imecatro.ui.products.views.ListOfProductsStateImpl
import com.imecatro.ui.products.views.fakeProductsList
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<ProductsViewModel>()
    val addProductViewModel by viewModels<AddViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                ProductsNavigation(viewModel,addProductViewModel)
            }
        }
    }
}

@Composable
fun MainApp(composable: @Composable () -> Unit) {
    PuntroSalesDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            composable()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PuntroSalesDemoTheme {

    }
}