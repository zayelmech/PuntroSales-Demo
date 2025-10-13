package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.core.files.FileInteractor
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import java.io.File

class ExportProductsCsvUseCase(
    private val productsRepository: ProductsRepository,
    private val fileInteractor: FileInteractor,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Ids, File>(coroutineProvider) {


    override suspend fun doInBackground(input: Ids): File {

        val csvLines =
            mutableListOf(listOf("Id", "Product", "Price", "Unit", "Stock"))

        val ids = Input().apply(input)

        val lines = productsRepository.getProductsWithIds(ids.ids)

        lines.forEach { l ->
            csvLines += listOf(
                l.id.toString(),
                l.name?:"-",
                l.price?.toString()?:"0.0",
                l.unit.toString(),
                l.stock.quantity.toString(),
            )
        }

        val file = fileInteractor.writeCsvToTickets("products_${System.currentTimeMillis()}.csv",csvLines)

        return file
    }


}

typealias Ids = Input.() -> Unit

data class Input(
    var ids: List<Long> = emptyList()
)