package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.core.date.convertMillisToDate
import com.imecatro.demosales.domain.sales.list.model.Ids
import com.imecatro.demosales.domain.sales.list.model.Input
import com.imecatro.demosales.domain.sales.list.repository.AllSalesRepository
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import java.io.File

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
}