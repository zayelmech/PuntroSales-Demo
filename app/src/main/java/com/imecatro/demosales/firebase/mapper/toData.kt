package com.imecatro.demosales.firebase.mapper

import com.imecatro.demosales.domain.products.usecases.WebCatalogDomainModel
import com.imecatro.demosales.firebase.dtos.WebCatalogDto
import com.imecatro.demosales.firebase.dtos.WebProductDto
import com.imecatro.demosales.firebase.dtos.WebStoreDto

/**
 * Extension function to convert a [WebCatalogDomainModel] to its data representation [WebCatalogDto].
 *
 * @param catalogId The unique identifier of the catalog (usually the user's UID).
 * @return A [WebCatalogDto] containing the mapped data.
 */
fun WebCatalogDomainModel.toData(catalogId: String): WebCatalogDto {
    return WebCatalogDto(
        catalogId = catalogId,
        template = template,
        store = WebStoreDto(
            name = store.name,
            description = store.description,
            logoUrl = store.logoUrl,
            currency = store.currency,
            whatsapp = store.whatsapp,
            location = store.location
        ),
        expiresAt = expiresAt,
        categories = categories,
        products = products.map { p ->
            WebProductDto(
                id = p.id,
                name = p.name,
                description = p.description,
                category = p.category,
                price = p.price,
                unit = p.unit,
                available = p.available,
                stock = p.stock,
                imageUrl = p.imageUrl
            )
        }
    )
}
