package com.imecatro.demosales.firebase.dtos

import kotlinx.serialization.Serializable

/**
 * DTO for store information in the web catalog.
 */
@Serializable
data class WebStoreDto(
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
@Serializable
data class WebProductDto(
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
@Serializable
data class WebCatalogDto(
    val catalogId: String,
    val template: String,
    val store: WebStoreDto,
    val expiresAt: String,
    val categories: List<String>,
    val products: List<WebProductDto>
)