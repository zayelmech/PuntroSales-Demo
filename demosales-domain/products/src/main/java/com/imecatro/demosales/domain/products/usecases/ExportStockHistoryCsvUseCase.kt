package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import java.io.File

class ExportStockHistoryCsvUseCase(
    private val productsRepository: ProductsRepository,
    private val fileInteractor: FileInteractor,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Long, File>(coroutineProvider) {


    override suspend fun doInBackground(input: Long): File {
     val history = productsRepository.getProductDetailsById(input)?.stock?.history

        val csvLines =
            mutableListOf(listOf("Id","Sale Id","date", "amount"))


        history?.forEach { l ->
            csvLines += listOf(
                l.tittle,
                l.date,
                l.qty.toString()
            )
        }
        val fileName = "product_${input}_${System.currentTimeMillis()}.csv"
        val file = fileInteractor.writeCsvToTickets(fileName, csvLines)
        return file
    }

}