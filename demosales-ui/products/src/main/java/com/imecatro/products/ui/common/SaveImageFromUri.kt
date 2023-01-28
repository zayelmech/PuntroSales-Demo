package com.imecatro.products.ui.common

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveMediaToStorage(context: Context?, bitmap: Bitmap, onUri: (Uri) -> Unit) {
    //Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"



    //Output stream
    var fos: OutputStream? = null

    //For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //getting the contentResolver
        context?.contentResolver?.also { resolver ->

            //Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                //putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                //put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            //Inserting the contentValues to contentResolver and getting the Uri
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                //resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            //Opening an outputstream with the Uri that we got
            //fos = imageUri?.let { resolver.openOutputStream(it) }
            //fos =imageUri?.let { resolver.openOutputStream(it,Context.MODE_PRIVATE)}

            //fos = context.openFileOutput(filename,Context.MODE_PRIVATE)
//            Log.d("TAG", "saveMediaToStorage: ${Environment.getExternalStorageDirectory()}" )
//            Log.d("TAG", "saveMediaToStorage: ${Environment.getDataDirectory()}" )
//            Log.d("TAG", "saveMediaToStorage: ${Environment.getExternalStoragePublicDirectory()}" )
            val file = File(
                context.filesDir,
                filename
            )
            fos = file.outputStream()
            //val uri = Uri.parse(context.getDir(filename,Context.MODE_PRIVATE).absolutePath)
            onUri(Uri.parse(file.absolutePath))
        }
    } else {
        //These for devices running on android < Q  -- 10
        //So I don't think an explanation is needed here
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        //context?.//toast("Saved to Photos")
    }

    fos?.close()
}
