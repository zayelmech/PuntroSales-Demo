package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase.Input
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

typealias Ids = Input.() -> Unit

class ExportSalesReportUseCase(
    private val allSalesRepository: AllSalesRepository,
    private val fileInteractor: FileInteractor,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Ids, File>(coroutineProvider) {


    override suspend fun doInBackground(input: Ids): File {
        val csvLines =
            mutableListOf(
                listOf("Id","Client","Client Address","Date","Status","Subtotal","Discount","Extra","Total","Notes"))

        val ids = Input().apply(input)

        val lines = allSalesRepository.getSalesWithIds(ids.ids)

        lines.forEach { l ->
            csvLines += listOf(
                l.id.toString(),
                l.clientName,
                l.clientAddress,
                l.date.toLong().convertMillisToDate(),
                l.status.str,
                l.totals.subTotal.toString(),
                l.totals.discount.toString(),
                l.totals.extraCost.toString(),
                l.totals.total.toString(),
                l.note
            )
        }

        val file =
            fileInteractor.writeCsvToTickets("sales_${System.currentTimeMillis()}.csv", csvLines)

        return file

    }

    data class Input(
        var ids: List<Long> = emptyList()
    )
}

fun Long.convertMillisToDate(): String {
    val instant = Instant.ofEpochMilli(this)
    // DateTimeFormatter.ISO_INSTANT correctly formats an Instant
    // to the full ISO 8601 format in UTC, ending with 'Z'.
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}
