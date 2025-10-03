package com.imecatro.demosales.di

import android.content.Context
import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModuleProvider {

    @Provides
    fun providesCoroutineDispatcher(): CoroutineProvider = CoroutineDispatcherImpl()


    @Provides
    fun providesFileInteractor(@ApplicationContext ctx: Context): FileInteractor =
        FileInteractorImpl(ctx)
}


class CoroutineDispatcherImpl : CoroutineProvider {
    override val io: CoroutineContext
        get() = Job() + Dispatchers.IO
    override val main: CoroutineContext
        get() = Job() + Dispatchers.Main

}


class FileInteractorImpl(private val ctx: Context) : FileInteractor {

    private fun ticketsDir(): File {
        val root = ctx.getExternalFilesDir(null) ?: ctx.filesDir
        val dir = File(root, "Tickets")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    override fun writeCsvToTickets(
        filename: String,
        rows: List<List<String>>
    ): File {
        val f = File(ticketsDir(), filename)
        f.writeText(CsvUtil.toCsv(rows), Charsets.UTF_8)
        return f
    }

}

object CsvUtil {
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
        return if (needs) "\"${'$'}body\"" else body
    }
}