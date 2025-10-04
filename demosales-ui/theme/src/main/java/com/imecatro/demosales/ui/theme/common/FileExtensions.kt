package com.imecatro.demosales.ui.theme.common

import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.util.Locale

fun File.share(context: Context){
    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider",this
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        clipData = ClipData.newUri(
            context.contentResolver,
            "File CSV",
            uri
        )
    }

    val chooser = Intent.createChooser(intent, "Share File")
    context.startActivity(chooser)
}


fun File.download(context: Context){
    val ext = MimeTypeMap.getFileExtensionFromUrl(name)?.lowercase(Locale.ROOT)
    val mime = when (ext) {
        "csv" -> "text/csv"
        "tsv" -> "text/tab-separated-values"
        else  -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext ?: "") ?: "application/octet-stream"
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // ANDROID 10+ — guardar en MediaStore.Downloads (aparece en Descargas)
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, mime)
            put(MediaStore.Downloads.SIZE, length())
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val destUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return

        resolver.openOutputStream(destUri)?.use { out ->
            inputStream().use { it.copyTo(out) }
        }

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(destUri, values, null, null)

    } else {
        // ANDROID 9- — copiar a /Download y registrar con DownloadManager
        @Suppress("DEPRECATION")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val target = File(downloadsDir, name)
        copyTo(target, overwrite = true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        @Suppress("DEPRECATION")
        dm.addCompletedDownload(
            /* title            = */ name,
            /* description      = */ "Archivo disponible en Descargas",
            /* isMediaScannerScannable */ false,
            /* mimeType         = */ mime,
            /* path             = */ target.absolutePath,
            /* length           = */ target.length(),
            /* showNotification = */ true
        )
    }

    context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
}

fun File.open(context: Context) {
    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", this
    )

    val mime = guessMimeType(this)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mime)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Si abre en modo edición, también: addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Open with"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "There is no app can open file extension .$extension", Toast.LENGTH_SHORT).show()
    }
}

private fun guessMimeType(file: File): String {
    val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)?.lowercase(Locale.ROOT)
    if (ext == "csv") return "text/csv"
    if (ext == "tsv") return "text/tab-separated-values"
    val byMap = ext?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
    return byMap ?: "application/octet-stream"
}