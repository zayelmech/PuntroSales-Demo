package com.imecatro.demosales.ui.theme.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream
import androidx.core.net.toUri
import java.util.Locale

inline fun Context.saveMediaToStorage(uriPicked: Uri, crossinline onUri: (Uri) -> Unit) {

    val bitmap = contentResolver.openInputStream(uriPicked)?.use(BitmapFactory::decodeStream)
        ?: return

    //Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"


    //Output stream

    val file = File(
        this.filesDir,
        filename
    )

    val fos: OutputStream = file.outputStream()

    fos.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
    }

    onUri(file.absolutePath.toUri())
}


fun Context.createImageFile(): Uri? {
    return try {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        
        // Use external storage if available, fallback to internal filesDir
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: filesDir
        
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", image)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
