package com.imecatro.products.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanBarcodeActivity : ComponentActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<androidx.camera.lifecycle.ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val previewView = androidx.camera.view.PreviewView(this)
        setContentView(previewView)

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(this)
        barcodeScanner = BarcodeScanning.getClient()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider, previewView)
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: androidx.camera.lifecycle.ProcessCameraProvider, previewView: androidx.camera.view.PreviewView) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: androidx.camera.core.CameraSelector =
            androidx.camera.core.CameraSelector.Builder()
            .requireLensFacing(androidx.camera.core.CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
            .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor, { imageProxy ->
            processImageProxy(imageProxy)
        })

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: androidx.camera.core.ImageProxy) {
        val image = imageProxy.image
        if (image != null) {
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { barcodeValue ->
                        val resultIntent = Intent().apply {
                            putExtra("barcode_result", barcodeValue)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}