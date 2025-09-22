package com.imecatro.demosales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imecatro.demosales.ui.AppAdaptiveNavigation
import com.imecatro.demosales.ui.MainScaffoldApp
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                // Experimental Suite Adaptive
                AppAdaptiveNavigation()
                //MainScaffoldApp()
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