package com.imecatro.demosales.di

import android.content.Context
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.files.FileInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext

/**
 * Hilt module for providing core application dependencies.
 *
 * This module provides common utilities such as coroutine dispatchers and file interaction
 * capabilities that are used throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModuleProvider {

    /**
     * Provides the implementation for [CoroutineProvider].
     *
     * @return An instance of [CoroutineDispatcherImpl].
     */
    @Provides
    fun providesCoroutineDispatcher(): CoroutineProvider = CoroutineDispatcherImpl()


    /**
     * Provides the implementation for [FileInteractor].
     *
     * @param ctx The application context.
     * @return An instance of [FileInteractorImpl].
     */
    @Provides
    fun providesFileInteractor(@ApplicationContext ctx: Context): FileInteractor =
        FileInteractorImpl(ctx)
}


/**
 * Implementation of [CoroutineProvider] using standard Kotlin Coroutine Dispatchers.
 *
 * Provides [Dispatchers.IO] for I/O operations and [Dispatchers.Main] for UI-related tasks.
 */
class CoroutineDispatcherImpl : CoroutineProvider {
    override val io: CoroutineContext
        get() = Dispatchers.IO + CoroutineName("Coroutine Provider IO")
    override val main: CoroutineContext
        get() = Dispatchers.Main + CoroutineName("Coroutine Provider Main")

}


/**
 * Implementation of [FileInteractor] for handling file system operations.
 *
 * This class provides methods for writing data to files within the application's
 * specific directories.
 *
 * @property ctx The application context used to access internal/external storage.
 */
class FileInteractorImpl(private val ctx: Context) : FileInteractor {

    private fun ticketsDir(): File {
        val root = ctx.getExternalFilesDir(null) ?: ctx.filesDir
        return File(root, "Tickets").also { it.mkdirs() }
    }

    /**
     * Writes a list of rows to a CSV file in the "Tickets" directory.
     *
     * @param filename The name of the file to be created.
     * @param rows The data to be written, structured as a list of string lists.
     * @return The [File] object representing the created CSV file.
     */
    override fun writeCsvToTickets(
        filename: String,
        rows: List<List<String>>
    ): File {
        val f = File(ticketsDir(), filename)
        val csvContent = CsvUtil.toCsv(rows)
        // 2. Usar FileOutputStream para tener control total sobre la escritura
        FileOutputStream(f).use { fos ->
            // 3. Escribir el BOM para UTF-8 al inicio del archivo
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
            // 4. Escribir el contenido del CSV usando la codificación UTF-8
            fos.write(csvContent.toByteArray(Charsets.UTF_8))
        }

        return f
    }

}

/**
 * Utility object for CSV-related operations.
 */
object CsvUtil {
    /**
     * Converts a list of string lists into a CSV-formatted string.
     *
     * @param lines The data to convert.
     * @return A string in CSV format.
     */
    fun toCsv(lines: List<List<String>>): String {
        return buildString {
            lines.forEachIndexed { i, row ->
                row.forEachIndexed { j, col ->
                    if (j > 0) append(',')
                    append(escape(col))
                }
                if (i < lines.lastIndex) append('\n')
            }
        }
    }

    private fun escape(s: String): String {
        val needs = s.contains(',') || s.contains('"') || s.contains('\n') || s.contains('\r')
        val body = s.replace("\"", "\"\"")
        return if (needs) """"$body"""" else body    }
}