package com.imecatro.demosales.domain.products.usecases

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.usecase.BackgroundUseCase
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.repository.ProductsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.net.URI
import java.util.Collections.emptyList

/**
 * Data class representing the form input for publishing a web catalog.
 */
data class CatalogForm(
    val storeName: String = "",
    val storeDescription: String = "",
    val whatsAppNumber: String,
    val location: String = "",
    val currency: String = "MXN",
    val template: String = "store",
    val enableStock: Boolean = false,
    val productsIds: List<Long> = emptyList()
)

/**
 * DTO for store information in the web catalog.
 */

data class WebStoreDomainModel(
    val name: String,
    val description: String,
    val logoUrl: String,
    val currency: String,
    val whatsapp: String,
    val location: String
)

/**
 * DTO for a single product in the web catalog.
 */

data class WebProductDomainModel(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val unit: String,
    val available: Boolean,
    val stock: Double,
    val imageUrl: String
)

/**
 * DTO for the entire web catalog structure.
 */

data class WebCatalogDomainModel(
    val catalogId: String,
    val template: String,
    val store: WebStoreDomainModel,
    val expiresAt: String,
    val categories: List<String>,
    val products: List<WebProductDomainModel>
)


data class PublishedCatalogDomainModel(
    val previewUrl: String, // for faster preview on webview
    val publicCatalogUrl: String, // official shared link for catalog
)

/**
 * Use case to publish a web catalog.
 *
 * It fetches products from the local repository, uploads their images to Firebase Storage,
 * generates a JSON representation of the catalog, uploads it, and returns the final
 * public URL for the web viewer.
 */
class PublishWebCatalogUseCase(
    private val catalogRepository: CatalogRepository,
    private val productsRepository: ProductsRepository,
    ioDispatcher: CoroutineProvider
) : BackgroundUseCase<CatalogForm, PublishedCatalogDomainModel>(ioDispatcher) {

    override suspend fun doInBackground(input: CatalogForm): PublishedCatalogDomainModel =
        coroutineScope {
            // 1. Fetch products from the local repository
            val products = productsRepository.getProductsWithIds(input.productsIds)

            // 2. Upload images in parallel and map domain models to DTOs
            val webProducts = products.map { product ->
                async {
                    val imageUrl =
                        product.imageUri?.let { uri -> catalogRepository.uploadImage(uri) } ?: ""

                    WebProductDomainModel(
                        id = product.id?.toString() ?: "",
                        name = product.name ?: "",
                        description = product.details,
                        category = product.category?.name ?: "General",
                        price = product.price ?: 0.0,
                        unit = product.unit ?: "Unit",
                        available = if (input.enableStock) product.stock.quantity > 0 else true,
                        stock = if (input.enableStock) product.stock.quantity else 0.0,
                        imageUrl = imageUrl
                    )
                }
            }.awaitAll()

            // 3. Build the final JSON structure based on the provided template
            val catalogDto = WebCatalogDomainModel(
                catalogId = "demo-catalog",
                template = input.template,
                store = WebStoreDomainModel(
                    name = input.storeName.ifBlank { "PuntroSales Demo Store" },
                    description = input.storeDescription.ifBlank { "Catálogo generado desde PuntroSales" },
                    logoUrl = "",
                    currency = input.currency,
                    whatsapp = input.whatsAppNumber,
                    location = input.location
                ),
                expiresAt = "2099-12-31T23:59:59-06:00",
                categories = listOf("Todos") + webProducts.map { it.category }.distinct(),
                products = webProducts
            )

            val jsonUrl = catalogRepository.uploadJsonCatalog(catalogDto)

            //catalogRepository.createsFinalUrl(jsonUrl)
            val previewURl = async { catalogRepository.previewUrl(jsonUrl) }
            val publicCatalogUrl = async { catalogRepository.createsFinalUrl(jsonUrl) }


            return@coroutineScope PublishedCatalogDomainModel(
                previewUrl = previewURl.await(),
                publicCatalogUrl = publicCatalogUrl.await()
            )
        }
}
