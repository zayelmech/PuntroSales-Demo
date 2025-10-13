package com.imecatro.products.ui.catalog.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

/**
 * Convierte un Uri de imagen a data URL base64, reescalando a maxWidth px.
 * Usa JPEG 85% para reducir tamaño. Devuelve "data:image/jpeg;base64,...."
 */
fun uriToDataUrlImage(
    context: Context,
    uri: Uri,
    maxWidth: Int = 1200,
    jpegQuality: Int = 85
): String? {
    return try {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= 28) {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // evita HW bitmaps
                val w = info.size.width
                val h = info.size.height
                if (w > 0 && h > 0) {
                    val scale = if (w > maxWidth) maxWidth / w.toFloat() else 1f
                    val tw = (w * scale).roundToInt().coerceAtLeast(1)
                    val th = (h * scale).roundToInt().coerceAtLeast(1)
                    decoder.setTargetSize(tw, th)
                }
            }
        } else {
            // API < 28: downsample
            context.contentResolver.openInputStream(uri).use { in1 ->
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(in1, null, bounds)
                val w = bounds.outWidth
                val sample = if (w > maxWidth) {
                    var s = 1
                    var half = w / 2
                    while ((half / s) >= maxWidth) s *= 2
                    s
                } else 1
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                context.contentResolver.openInputStream(uri).use { in2 ->
                    BitmapFactory.decodeStream(in2, null, opts)!!
                }
            }
        }

        ByteArrayOutputStream().use { bos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, bos)
            val b64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
            "data:image/jpeg;base64,$b64"
        }
    } catch (e: Exception) {
        Log.e(TAG, "uriToDataUrlImage: ", e)
        null
    }
}

private const val TAG = "ImageExt"