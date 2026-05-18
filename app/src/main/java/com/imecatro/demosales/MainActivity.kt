package com.imecatro.demosales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imecatro.demosales.ui.AppAdaptiveNavigation
import com.imecatro.demosales.ui.theme.PuntroSalesDemoTheme
import com.imecatro.demosales.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the application.
 *
 * This activity is annotated with [AndroidEntryPoint] to enable Hilt dependency injection.
 * It serves as the host for the Compose UI and manages the initial configuration,
 * including edge-to-edge display and the root navigation structure.
 *
 * Applies the [PuntroSalesDemoTheme] and sets up a [Surface] with the theme's background color.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = uiState.isDarkTheme ?: isSystemInDarkTheme()

            PuntroSalesDemoTheme(darkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppAdaptiveNavigation()
                }
            }
        }
    }
}