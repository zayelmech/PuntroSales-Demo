package com.imecatro.products.ui.list.images

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import coil.size.Precision
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicInteger

/** Disposable derived images. Original product photos are never rewritten. */
object ProductThumbnails {
    private const val MAX_BYTES = 64L * 1024 * 1024
    private val decoders = Semaphore(2)
    private val locks = Array(32) { Mutex() }
    private val writes = AtomicInteger()

    suspend fun uri(context: Context, original: Uri, pixels: Int): Uri = withContext(Dispatchers.IO) {
        // App imports are local files. Providers without a stable file version use Coil directly.
        if (original.toString().isBlank() || original.scheme !in listOf(null, "file")) return@withContext original
        val source = File(original.path ?: return@withContext original)
        if (!source.isFile) return@withContext original
        val identity = "v1|${source.absolutePath}|${source.lastModified()}|${source.length()}|$pixels"
        val digest = MessageDigest.getInstance("SHA-256").digest(identity.toByteArray())
        val key = digest.joinToString("") { "%02x".format(it) }
        locks[(digest[0].toInt() and 0xff) % locks.size].withLock {
            val directory = File(context.cacheDir, "product-thumbnails")
            val target = File(directory, "$key.jpg")
            if (target.isFile && target.length() > 0) return@withLock target.toUri()
            decoders.withPermit {
                var temporary: File? = null
                try {
                    if (!directory.isDirectory && !directory.mkdirs() && !directory.isDirectory) return@withPermit original
                    val result = context.imageLoader.execute(ImageRequest.Builder(context)
                        .data(original).size(pixels, pixels).scale(Scale.FIT).precision(Precision.EXACT)
                        .allowHardware(false).build()) as? SuccessResult
                        ?: return@withPermit original
                    temporary = File.createTempFile("thumb-", ".tmp", directory)
                    val compressed = temporary.outputStream().use {
                        result.drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 85, it)
                    }
                    if (!compressed || !temporary.renameTo(target)) return@withPermit original
                    if (writes.incrementAndGet() % 32 == 0) trim(directory)
                    target.toUri()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    original // A cache failure must never hide an otherwise readable photo.
                } finally {
                    temporary?.delete()
                }
            }
        }
    }

    private fun trim(directory: File) {
        val files = directory.listFiles { file -> file.extension == "jpg" } ?: return
        var bytes = files.sumOf { it.length() }
        for (file in files.sortedBy { it.lastModified() }) {
            if (bytes <= MAX_BYTES) break
            val size = file.length()
            if (file.delete()) bytes -= size
        }
    }
}
