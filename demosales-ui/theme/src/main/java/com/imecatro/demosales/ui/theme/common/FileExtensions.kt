package com.imecatro.demosales.ui.theme.common

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

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
        context, "${context.packageName}.fileprovider",this
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        type = "text/csv"
        addCategory(Intent.CATEGORY_OPENABLE)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(intent, "Share File")
    context.startActivity(chooser)

}
