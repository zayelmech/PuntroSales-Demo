package com.imecatro.demosales.ui.theme.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream
import androidx.core.net.toUri
import java.util.Locale

inline fun Context.saveMediaToStorage(uriPicked: Uri, crossinline onUri: (Uri) -> Unit) {

    lateinit var bitmap: Bitmap

    if (Build.VERSION.SDK_INT < 28) {
        bitmap = MediaStore.Images
            .Media.getBitmap(contentResolver, uriPicked)

    } else {
        val source = ImageDecoder
            .createSource(contentResolver, uriPicked)
        bitmap = ImageDecoder.decodeBitmap(source) //.scale(500, 500)
    }

    //Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"


    //Output stream

    val file = File(
        this.filesDir,
        filename
    )

    val fos: OutputStream = file.outputStream()

    onUri(file.absolutePath.toUri())


    fos.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        //context?.//toast("Saved to Photos")
    }

    fos.close()
}


fun Context.createImageFile(): Uri? {
    // Create an image file name
    val timeStamp =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = java.io.File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    )
    return FileProvider.getUriForFile(this, "${packageName}.fileprovider", image)
}
