package com.imecatro.products.ui.catalog.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.imecatro.products.ui.R
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Preview
@Composable
fun SavePdfButton(html: String = "") {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val htmlToPdfConvertor = remember { HtmlToPdfConvertor(context) }
    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        // Creamos un archivo temporal en el caché de la app donde la biblioteca guardará el PDF.
        if (uri != null) {
            val tempFile = File.createTempFile("report_", ".pdf", context.cacheDir)

            scope.launch {
                htmlToPdfConvertor.convert(
                    pdfLocation = tempFile, // La biblioteca necesita un objeto File para escribir.
                    htmlString = html,
                    onPdfGenerationFailed = { exception ->
                        exception.printStackTrace()
                        // Informar al usuario del error.
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    onPdfGenerated = { pdfFile ->
                        // El PDF se generó en el archivo temporal (pdfFile).
                        // Ahora, copiamos el contenido de ese archivo a la Uri que el usuario seleccionó.
                        scope.launch(Dispatchers.IO) {
                            try {
                                context.contentResolver.openOutputStream(uri).use { outputStream ->
                                    pdfFile.inputStream().use { inputStream ->
                                        inputStream.copyTo(outputStream!!)
                                    }
                                }
                                // Opcional: Borramos el archivo temporal una vez copiado.
                                pdfFile.delete()

                                // Informamos al usuario que el archivo se guardó.
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "PDF guardado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Error al guardar el archivo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
            }
        }
    }

    Button(onClick = {
        saveLauncher.launch("Catalog_${System.currentTimeMillis()}.pdf")
    }) {
        Text(stringResource(R.string.btn_save_pdf))
    }
}