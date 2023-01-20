package com.imecatro.demosales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.ui.products.model.ProductUiModel
import com.imecatro.ui.products.views.BottomSheetDetailsCompose
import com.imecatro.ui.products.views.ListOfProducts
import com.imecatro.ui.products.views.fakeProductsList
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                val scope = rememberCoroutineScope()
                val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                val fakeList = fakeProductsList(20)
                var productSelected: ProductUiModel by remember {
                    mutableStateOf(fakeList.first())
                }

                BottomSheetDetailsCompose(productSelected, state, {}, {}) {

                    ListOfProducts(list = fakeList) {id ->
                        scope.launch {
                           productSelected = fakeList.find { it.id ==  id}?:fakeList.first()
                            state.show()
                        }
                    }
                }

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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PuntroSalesDemoTheme {
        Greeting("Android")
    }
}