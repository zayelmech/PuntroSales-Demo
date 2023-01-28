package com.imecatro.products.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.OutputStream

fun saveMediaToStorage(context: Context?, bitmap: Bitmap, onUri: (Uri) -> Unit) {
    //Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"


    //Output stream
    var fos: OutputStream? = null

    context?.let { c ->

        val file = File(
            c.filesDir,
            filename
        )
        fos = file.outputStream()
        //val uri = Uri.parse(context.getDir(filename,Context.MODE_PRIVATE).absolutePath)
        onUri(Uri.parse(file.absolutePath))
    }

    fos?.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        //context?.//toast("Saved to Photos")
    }

    fos?.close()
}
