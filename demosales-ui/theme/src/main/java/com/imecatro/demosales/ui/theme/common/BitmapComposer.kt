package com.imecatro.demosales.ui.theme.common

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.math.roundToInt
import coil.ImageLoader
import coil.EventListener
import coil.compose.LocalImageLoader
import kotlinx.coroutines.withContext

/**
 * Draws an arbitrary composable into a bitmap
 * mainScope has to be Dispatcher.Main because it has to perform
 * layout and measurement calculations on the UI thread.
 *
 * @see <a href="https://github.com/eclypse-tms/bitmap-composer" >doc</a>
 */
class BitmapComposer(private val mainScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)) {
    /**
     * Renders an arbitrary Composable View into a Bitmap.
     *
     * @param activity The host activity that is needed to attach the composable content to the view hierarchy.
     * @param width Optional width of the bitmap in device-independent pixels. Try to provide
     * a width or height value for better results.
     * @param height Optional height of the bitmap in device-independent pixels. Try to provide
     * a width or height value for better results.
     * @param screenDensity screen density to interpret the width and height.
     * @param content An arbitrary composable content to render.
     * @return A Bitmap representing the rendered Composable content.
     */
    suspend fun composableToBitmap(
        activity: Activity,
        width: Dp? = null,
        height: Dp? = null,
        screenDensity: Density,
        content: @Composable () -> Unit
    ): Bitmap = suspendCancellableCoroutine { continuation ->
        mainScope.launch {
            // Step 1: Interpret the pixels while taking into account the screen density
            val contentWidthInPixels =
                (screenDensity.density * (width ?: 3000.dp).value).roundToInt()
            val contentHeightInPixels =
                (screenDensity.density * (height ?: 3000.dp).value).roundToInt()

            // Step 2: Create a container to hold the ComposeView temporarily
            val composeViewContainer = FrameLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(contentWidthInPixels, contentHeightInPixels)
                visibility = View.INVISIBLE // Keep it invisible
            }

            // Step 3: Create and configure the ComposeView using the activity
            val composeView = ComposeView(activity).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent { content() }
            }

            // add the composable view to the container
            composeViewContainer.addView(composeView)

            // Step 4: Attach container to the root decor view
            val decorView = activity.window.decorView as ViewGroup
            decorView.addView(composeViewContainer) // since the container is invisible, we are OK.

            // Step 5: Create measure specifications for the ComposeView
            // If height or width is not provided, use AT_MOST to let the content decide the height
            val widthMeasureSpecs = if (width == null) {
                View.MeasureSpec.AT_MOST // or View.MeasureSpec.UNSPECIFIED
                // UNSPECIFIED width may not work with horizontally scrollable content - use caution.
            } else {
                View.MeasureSpec.EXACTLY
            }

            val heightMeasureSpecs = if (height == null) {
                View.MeasureSpec.AT_MOST // or View.MeasureSpec.UNSPECIFIED
                // UNSPECIFIED height may not work with vertically scrollable content - use caution.
            } else {
                View.MeasureSpec.EXACTLY
            }

            // Step 5: Wait for the ComposeView to be drawn and capture the bitmap
            Handler(Looper.getMainLooper()).post {
                // ask for the container view to measure itself
                composeViewContainer.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        contentWidthInPixels,
                        widthMeasureSpecs
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        contentHeightInPixels,
                        heightMeasureSpecs
                    )
                )

                // now request a layout at origin
                composeViewContainer.layout(0, 0, contentWidthInPixels, contentHeightInPixels)

                val bitmap = composeView.drawToBitmap() // layout finished, draw to bitmap
                continuation.resume(bitmap) // notify the caller with the bitmap

                // Step 6: Clean up - remove the container
                decorView.removeView(composeViewContainer)
            }
        }
    }
    /**
     * Variante robusta para contenido con Coil: espera a que todas las requests
     * terminen y captura *después* del siguiente pre-draw.
     */
    suspend fun composableToBitmapCoilSafe(
        activity: Activity,
        width: Dp? = null,
        height: Dp? = null,
        screenDensity: Density,
        content: @Composable () -> Unit
    ): Bitmap = suspendCancellableCoroutine { continuation ->
        mainScope.launch {
            val contentWidthPx = (screenDensity.density * (width ?: 3000.dp).value).roundToInt()
            val contentHeightPx = (screenDensity.density * (height ?: 3000.dp).value).roundToInt()

            // 1) Contenedor off-screen pero visible (alpha 0)
            val container = FrameLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(contentWidthPx, contentHeightPx)
                alpha = 0f
                visibility = View.VISIBLE
            }

            // 2) Contadores Coil
            val active = AtomicInteger(0)
            val sawAny = AtomicBoolean(false)
            val listener = object : EventListener {
                override fun onStart(request: coil.request.ImageRequest) {
                    sawAny.set(true); active.incrementAndGet()
                }
                override fun onCancel(request: coil.request.ImageRequest) {
                    active.decrementAndGet()
                }
                override fun onError(request: coil.request.ImageRequest, result: coil.request.ErrorResult) {
                    active.decrementAndGet()
                }
                override fun onSuccess(request: coil.request.ImageRequest, result: coil.request.SuccessResult) {
                    active.decrementAndGet()
                }
            }
            val loader = ImageLoader.Builder(activity)
                .allowHardware(false)                       // <- IMPORTANTE
                .bitmapConfig(android.graphics.Bitmap.Config.ARGB_8888)
                .memoryCachePolicy(coil.request.CachePolicy.DISABLED) // evita recuperar HW del cache
                .eventListener(listener)
                .crossfade(false)
                .build()

            // 3) ComposeView con ImageLoader inyectado
            val composeView = ComposeView(activity).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    CompositionLocalProvider(LocalImageLoader provides loader) {
                        content()
                    }
                }
            }
            container.addView(composeView)

            // 4) Adjuntar, medir y hacer layout en el hilo principal
            val decor = activity.window.decorView as ViewGroup
            decor.addView(container)

            Handler(Looper.getMainLooper()).post {
                container.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        contentWidthPx, if (width == null) View.MeasureSpec.AT_MOST else View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        contentHeightPx, if (height == null) View.MeasureSpec.AT_MOST else View.MeasureSpec.EXACTLY
                    )
                )
                container.layout(0, 0, contentWidthPx, contentHeightPx)
            }

            // 5) Esperar a Coil y al siguiente pre-draw real
            try {
                withTimeout(12_000) {
                    // da un frame para que arranquen las requests
                    delay(16)
                    // esperar hasta que no haya requests activas (si es que hubo)
                    while (sawAny.get() && active.get() > 0) {
                        delay(16)
                    }
                    // invalidar y esperar a que la vista haga el siguiente pre-draw
                    withContext(Dispatchers.Main) { composeView.invalidate() }
                    composeView.awaitNextPreDraw()
                    // un frame extra por seguridad
                    delay(16)
                }
            } catch (_: Exception) {
                // timeout: seguimos con lo que haya
            }

            // 6) Capturar
            Handler(Looper.getMainLooper()).post {
                val bmp = composeView.drawToBitmap()
                if (continuation.isActive) continuation.resume(bmp)
                decor.removeView(container)
                loader.shutdown()
            }
        }
    }
}

/** Suspende hasta el próximo onPreDraw del View. */
private suspend fun View.awaitNextPreDraw() = suspendCancellableCoroutine<Unit> { cont ->
    val vto = viewTreeObserver
    val listener = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (vto.isAlive) vto.removeOnPreDrawListener(this)
            if (cont.isActive) cont.resume(Unit)
            return true
        }
    }
    vto.addOnPreDrawListener(listener)
    cont.invokeOnCancellation {
        if (vto.isAlive) vto.removeOnPreDrawListener(listener)
    }
}