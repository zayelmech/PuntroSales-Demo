package com.imecatro.demosales.ai

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.imecatro.demosales.domain.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.model.ProductStockDomainModel
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "TaskAppFunctionService",
    appFunctionXmlFileName = "task_app_function_service",
)
abstract class BaseTaskAppFunctionService : AppFunctionService() {

    @Inject
    lateinit var productsRepository: ProductsRepository

    @AppFunction(isDescribedByKDoc = true)
    suspend fun createsProduct(name: String, price: Double, stock: Double) = withContext(Dispatchers.IO) {
        productsRepository.addProduct(
            ProductDomainModel(
                id = null,
                name = name,
                price = price,
                currency = "USD",
                unit = "pz",
                stock = ProductStockDomainModel(quantity = stock, cost = 0.0, emptyList()),
                details = "Added with IA",
                imageUri = null,
                category = null,
                barcode = null
            )
        )
    }

}