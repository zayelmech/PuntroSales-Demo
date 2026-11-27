package com.imecatro.demosales.ui.sales.list

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.ui.theme.common.downloadInBackground
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

class SalesReportDownloadTest {
    @Test fun copiesReportOffMainBeforeOpeningDownloadsOnMain() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("sales-perf-", ".csv", target.cacheDir)
        val contents = "id,total\n1,20.00\n"
        file.writeText(contents)
        var copiedOffMain = false
        var openedDownloads = false
        var downloadedUri: Uri? = null
        val context = object : ContextWrapper(target) {
            override fun getContentResolver(): ContentResolver {
                assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
                copiedOffMain = true
                return target.contentResolver
            }

            override fun startActivity(intent: Intent) {
                assertEquals(Looper.getMainLooper(), Looper.myLooper())
                assertEquals(DownloadManager.ACTION_VIEW_DOWNLOADS, intent.action)
                assertTrue(copiedOffMain)
                openedDownloads = true
            }
        }
        try {
            runBlocking { file.downloadInBackground(context) }
            assertTrue(openedDownloads)
            target.contentResolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Downloads._ID),
                "${MediaStore.Downloads.DISPLAY_NAME} = ?",
                arrayOf(file.name),
                null
            )!!.use { cursor ->
                assertTrue(cursor.moveToFirst())
                downloadedUri = ContentUris.withAppendedId(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, cursor.getLong(0)
                )
            }
            val savedText = target.contentResolver.openInputStream(downloadedUri!!)!!
                .bufferedReader().use { it.readText() }
            assertEquals(contents, savedText)
        } finally {
            downloadedUri?.let { target.contentResolver.delete(it, null, null) }
            file.delete()
        }
    }
}
