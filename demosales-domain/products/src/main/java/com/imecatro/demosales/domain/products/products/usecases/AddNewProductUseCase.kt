package com.imecatro.demosales.domain.products.products.usecases

import com.imecatro.demosales.domain.products.products.model.ProductDomainModel
import com.imecatro.demosales.domain.products.products.repository.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Add new product use case
 * This use case is handling the info from UI and sending the data to data layer
 *the purpose is to save a product into room database
 * @property productsRepository this the interface that must be implemented into the data layer
 * @property dispatcher this is the dispatcher where the coroutines will perform the action in background or testing thread
 * @constructor Create empty Add new product use case
 */
class AddNewProductUseCase(
    private val productsRepository: ProductsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Invoke
     *
     * @param product this is a data class that contains the data
     * @return
     */
    suspend operator fun invoke(product: ProductDomainModel): State =
       withContext(dispatcher) {
            return@withContext try {
                 productsRepository.addProduct(product)
                State(1,null)
            } catch (e: java.lang.Exception) {
                State(null, e.message)
            }
        }

}

/**
 * State
 *
 * @property id
 * @property error
 * @constructor Create empty State
 */
data class State(
    val id: Int?,
    val error: String?
)
