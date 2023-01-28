package com.imecatro.demosales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.demosales.navigation.ProductsNavigation
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.products.ui.add.viewmodel.AddViewModel
import com.imecatro.products.ui.details.viewmodels.ProductsDetailsViewModel
import com.imecatro.products.ui.list.viewmodels.ProductsViewModel
import com.imecatro.products.ui.update.viewmodel.UpdateProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //val repository : DummyRepository = DummyRepository
   // val viewModel :ProductsViewModel = hiltViewModel()
//    val addProductViewModel by viewModels<AddViewModel>()
//    val productDetailsViewModel by viewModels<ProductsDetailsViewModel>()
//    val updateProductViewModel by viewModels<UpdateProductViewModel>()

    @Inject
    lateinit var  viewModel :ProductsViewModel
    @Inject
    lateinit var addProductViewModel : AddViewModel
    @Inject
    lateinit var productDetailsViewModel : ProductsDetailsViewModel
    @Inject
    lateinit var  updateProductViewModel : UpdateProductViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainApp {
//                val viewModel :ProductsViewModel = hiltViewModel()
//                val addProductViewModel : AddViewModel = hiltViewModel()
//                val productDetailsViewModel : ProductsDetailsViewModel = hiltViewModel()
//                val updateProductViewModel : UpdateProductViewModel = hiltViewModel()

                ProductsNavigation(viewModel,productDetailsViewModel,addProductViewModel,updateProductViewModel)
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