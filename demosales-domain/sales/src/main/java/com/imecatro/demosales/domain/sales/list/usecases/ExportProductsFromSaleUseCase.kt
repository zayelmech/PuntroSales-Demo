package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.list.model.Ids
import com.imecatro.demosales.domain.sales.list.model.Input
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import com.imecatro.demosales.domain.sales.model.Order
import java.io.File

class ExportProductsFromSaleUseCase(
    private val detailsSaleRepository: DetailsSaleRepository,
    private val fileInteractor: FileInteractor,
    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Ids, File>(coroutineProvider) {


    override suspend fun doInBackground(input: Ids): File {
        val csvLines =
            mutableListOf(listOf("Id", "Producto", "Unidad", "Cantidad", "Precio unitario"))
        val ids = Input().apply(input)

        val allProducts: MutableList<Order> = mutableListOf()

        ids.ids.forEach { id ->
            val s = detailsSaleRepository.getSaleDetailsById(id).list
            allProducts += s
        }

        val groupedById = allProducts.groupBy { it.productId }

        groupedById.forEach { p ->
            val first = p.value.first()
            val row = listOf<String>(
                first.productId.toString(),
                first.productName,
                "Unit",
                p.value.sumOf { it.qty }.toString(),
                first.productPrice.toString()
            )
            csvLines += row
        }
        val file =
            fileInteractor.writeCsvToTickets("prducts_${System.currentTimeMillis()}.csv", csvLines)

        return file

    }
}