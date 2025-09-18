package com.imecatro.products.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProductFullDetailsRoomModel(
    @Embedded val product: ProductRoomEntity,

    // Optional 1–1: products.category_id -> categories.id
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryRoomEntity?,

    // 1–N: products.id -> stock.product_id
    @Relation(
        parentColumn = "id",
        entityColumn = "product_id",
        entity = StockRoomEntity::class
    )
    val stock: List<StockRoomEntity>
)



/**
 * Data class representing the full details of a product, including its associated category and stock information,
 * designed for Room persistence.
 *
 * This class uses `@Embedded` to include the `ProductRoomEntity` directly within its structure.
 * It also defines relationships with `CategoryRoomEntity` (one-to-one, optional) and `StockRoomEntity` (one-to-many).
 *
 * @property product The core product information, embedded within this model.
 * @property category The category associated with the product. This is an optional one-to-one relationship,
 * meaning a product might not have a category. The relationship is established by matching
 * `product.category_id` with `category.id`.
 */
data class ProductAnCategoryDetailsRoomModel(
    @Embedded val product: ProductRoomEntity,

    // Optional 1–1: products.category_id -> categories.id
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryRoomEntity?,
)