package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.sales.details.DetailsSaleRepository
import com.imecatro.demosales.domain.sales.list.model.Ids
import com.imecatro.demosales.domain.sales.list.model.Input
import com.imecatro.demosales.domain.sales.list.repository.FileInteractor
import com.imecatro.demosales.domain.sales.model.Order
import java.io.File

/**
 * A use case responsible for exporting a consolidated list of products from multiple sales into a CSV file.
 *
 * This use case takes a list of sale IDs as input, retrieves the product details for each sale,
 * groups the products by their ID, sums their quantities, and then writes the aggregated data
 * into a CSV file. The resulting file includes columns for product ID, name, unit, total quantity,
 * and unit price.
 *
 * @param detailsSaleRepository Repository to fetch the details of each sale.
 * @param fileInteractor Interactor to handle file operations, specifically writing the CSV data.
 * @param coroutineProvider Provides the coroutine context for executing the background task.
 * @property Ids The input type, a data class containing a list of sale IDs to process.
 * @property File The output type, representing the generated CSV file.
 */
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