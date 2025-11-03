package com.imecatro.demosales.ui.theme.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Vista de cámara en Compose con:
 * - Preview
 * - Torch toggle
 * - (Opcional) análisis por frame para códigos de barras (ML Kit)
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    enableAnalysis: Boolean = false,
    analysisTargetSize: Size = Size(1280, 720),
    onBarcode: (String) -> Unit = {},
    onReady: (Camera) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var torchOn by remember { mutableStateOf(false) }

    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember {
        ProcessCameraProvider.getInstance(context)
    }

    // This ensures that the onBarcodes callback used inside the analysis is always the latest one.
    val onBarcodesUpdated by rememberUpdatedState(onBarcode)

    // Remember the camera executor
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            // It's a good practice to shut down the executor when the composable is disposed
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            if (!hasCameraPermission) return@AndroidView

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val selector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.unbindAll()

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val analysis = if (enableAnalysis) {
                    val scanner = BarcodeScanning.getClient()
                    ImageAnalysis.Builder()
                        .setTargetResolution(analysisTargetSize)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().apply {
                            setAnalyzer(cameraExecutor) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val img = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                    scanner.process(img)
                                        .addOnSuccessListener { result ->
                                            if (result.isNotEmpty()) onBarcodesUpdated(result.firstOrNull()?.rawValue?:"" )
                                        }
                                        .addOnCompleteListener { imageProxy.close() }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }
                } else null

                camera = if (analysis != null)
                    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
                else
                    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)

                camera?.let(onReady)

                camera?.cameraInfo?.hasFlashUnit()?.let { hasFlash ->
                    if (hasFlash) camera?.cameraControl?.enableTorch(torchOn)
                }

            }, ContextCompat.getMainExecutor(context))
        }
    )
}
