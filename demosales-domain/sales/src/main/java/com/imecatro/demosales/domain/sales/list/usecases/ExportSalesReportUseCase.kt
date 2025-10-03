package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import com.imecatro.demosales.domain.sales.list.usecases.ExportSalesReportUseCase.Input
import kotlinx.coroutines.flow.first
import java.io.File


typealias Ids = Input.() -> Unit

class ExportSalesReportUseCase(
    private val allSalesRepository: AllSalesRepository,
    private val fileInteractor: FileInteractor,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Ids, File>(coroutineProvider) {


    override suspend fun doInBackground(input: Ids): File {
        val csvLines =
            mutableListOf(
                listOf(
                    "Id",
                    "Client",
                    "Date",
                    "Status",
                    "No. items",
                    "Subtotal",
                    "Discount",
                    "Extra",
                    "Total",
                    "Notes"
                )
            )

        val ids = Input().apply(input)

        val lines = allSalesRepository.getAllSales().first().filter { it.id in ids.ids }

        lines.forEach { l ->
            csvLines += listOf(
                l.id.toString(),
                l.clientName,
                l.date.toString(),
                l.status.str,
                l.total.toString()
            )
        }

        val file =
            fileInteractor.writeCsvToTickets("sales${System.currentTimeMillis()}.csv", csvLines)

        return file

    }

    data class Input(
        var ids: List<Long> = emptyList()
    )
}
