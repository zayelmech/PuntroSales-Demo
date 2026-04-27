package com.imecatro.demosales

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base class for maintaining global application state.
 *
 * This class is annotated with [HiltAndroidApp] to trigger Hilt's code generation,
 * including a base class for your application that serves as the application-level
 * dependency container.
 */
@HiltAndroidApp
class PuntroSalesDemoApp :Application() {
}