package com.imecatro.demosales.ui.theme.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
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
    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider",this
    )


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

    // (Opcional pero robusto) Conceder permiso a todas las apps que puedan manejarlo
    val resInfoList = context.packageManager.queryIntentActivities(intent, 0)
    for (res in resInfoList) {
        context.grantUriPermission(
            res.activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Abrir con"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay apps para abrir .$extension", Toast.LENGTH_SHORT).show()
    }
}

private fun guessMimeType(file: File): String {
    val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)?.lowercase(Locale.ROOT)
    // Ajustes comunes
    if (ext == "csv") return "text/csv"
    if (ext == "tsv") return "text/tab-separated-values"
    val byMap = ext?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
    return byMap ?: "application/octet-stream"
}