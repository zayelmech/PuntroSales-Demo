package com.imecatro.demosales.domain.sales.list.usecases;

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase

class ExportProductsFromSaleUseCase(

    coroutineProvider: CoroutineProvider
) : BackgroundUseCase<Unit, String?>(coroutineProvider) {


    override suspend fun doInBackground(input: Unit): String? {
        //val csvLines = mutableListOf(listOf("Id","Producto","Unidad","Cantidad","Precio unitario","Total","Pzs"))

        TODO()

    }
}
