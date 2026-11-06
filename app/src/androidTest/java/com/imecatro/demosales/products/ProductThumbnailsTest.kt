package com.imecatro.demosales.products

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.products.ui.list.images.ProductThumbnails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProductThumbnailsTest {
    @Test fun derivedImageIsBoundedReusedAndVersionedWithoutChangingTheOriginal() = runBlocking(Dispatchers.IO) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = File.createTempFile("test-product-photo", ".jpg", context.cacheDir)
        val created = mutableListOf<File>()
        try {
            fun photo(color: Int) {
                val bitmap = Bitmap.createBitmap(1_000, 750, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(color)
                source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
                bitmap.recycle()
            }
            photo(Color.RED)
            val originalBytes = source.readBytes()
            val first = ProductThumbnails.uri(context, source.toUri(), 96)
            val thumbnail = File(first.path!!).also { created += it }
            assertNotEquals(source.toUri(), first)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(thumbnail.path, bounds)
            assertTrue(bounds.outWidth in 1..96)
            assertTrue(bounds.outHeight in 1..96)
            assertArrayEquals(originalBytes, source.readBytes())
            val modified = thumbnail.lastModified()
            assertEquals(first, ProductThumbnails.uri(context, source.toUri(), 96))
            assertEquals(modified, thumbnail.lastModified())
            val larger = ProductThumbnails.uri(context, source.toUri(), 192)
            created += File(larger.path!!)
            assertNotEquals(first, larger)
            photo(Color.BLUE)
            assertTrue(source.setLastModified(source.lastModified() + 2_000))
            val updated = ProductThumbnails.uri(context, source.toUri(), 96)
            created += File(updated.path!!)
            assertNotEquals(first, updated)
        } finally {
            source.delete()
            created.forEach { it.delete() }
        }
    }

    @Test fun missingFilesAndExternalProvidersFallBackToTheOriginalUri() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (uri in listOf(File(context.cacheDir, "missing-photo.jpg").toUri(), Uri.parse("content://test-provider/photo"))) {
            assertEquals(uri, ProductThumbnails.uri(context, uri, 96))
        }
    }
}
