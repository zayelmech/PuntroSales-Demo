package com.imecatro.demosales.firebase.mapper

import com.imecatro.demosales.domain.products.usecases.WebCatalogDomainModel
import com.imecatro.demosales.domain.products.usecases.WebProductDomainModel
import com.imecatro.demosales.domain.products.usecases.WebStoreDomainModel
import com.imecatro.demosales.firebase.dtos.WebCatalogDto
import com.imecatro.demosales.firebase.dtos.WebProductDto
import com.imecatro.demosales.firebase.dtos.WebStoreDto

/**
 * Extension function to convert a [WebCatalogDto] to its domain representation [WebCatalogDomainModel].
 */
fun WebCatalogDto.toDomain(): WebCatalogDomainModel {
    return WebCatalogDomainModel(
        catalogId = catalogId,
        template = template,
        store = store.toDomain(),
        expiresAt = expiresAt,
        categories = categories,
        products = products.map { it.toDomain() }
    )
}

/**
 * Extension function to convert a [WebStoreDto] to its domain representation [WebStoreDomainModel].
 */
fun WebStoreDto.toDomain(): WebStoreDomainModel {
    return WebStoreDomainModel(
        name = name,
        description = description,
        logoUrl = logoUrl,
        currency = currency,
        whatsapp = whatsapp,
        location = location
    )
}

/**
 * Extension function to convert a [WebProductDto] to its domain representation [WebProductDomainModel].
 */
fun WebProductDto.toDomain(): WebProductDomainModel {
    return WebProductDomainModel(
        id = id,
        name = name,
        description = description,
        category = category,
        price = price,
        unit = unit,
        available = available,
        stock = stock,
        imageUrl = imageUrl
    )
}
