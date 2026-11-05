package com.imecatro.products.data.paging

import androidx.sqlite.db.SimpleSQLiteQuery
import com.imecatro.demosales.domain.products.model.ProductOrder
import com.imecatro.demosales.domain.products.model.ProductsQuery
import com.imecatro.products.data.model.ProductListRow

data class ProductCursor(
    val id: Long,
    val nameKey: String,
    val price: Double,
    val stock: Double,
    val before: Boolean = false,
    val anchor: Boolean = false,
) {
    companion object {
        fun from(row: ProductListRow, before: Boolean = false) =
            ProductCursor(row.id, row.nameKey, row.price, row.stock, before)
    }
}

/** SQL structure comes exclusively from enums; every user value is bound. */
internal class ProductListQueries(private val query: ProductsQuery) {
    private val column = when (query.order) {
        ProductOrder.NAME -> "nameKey"
        ProductOrder.PRICE -> "price"
        ProductOrder.STOCK -> "stock"
        ProductOrder.CREATED -> "id"
    }

    fun rows(limit: Int, cursor: ProductCursor? = null, inclusive: Boolean = false): SimpleSQLiteQuery {
        val (conditions, args) = filters()
        if (cursor != null) {
            val operator = if (query.descending xor cursor.before) "<" else ">"
            val comparison = operator + if (inclusive) "=" else ""
            if (column == "id") {
                conditions += "p.id $comparison ?"
                args += cursor.id
            } else {
                conditions += "(p.$column, p.id) $comparison (?, ?)"
                args += when (query.order) {
                    ProductOrder.NAME -> cursor.nameKey
                    ProductOrder.PRICE -> cursor.price
                    ProductOrder.STOCK -> cursor.stock
                    ProductOrder.CREATED -> cursor.id
                }
                args += cursor.id
            }
        }
        val direction = if (query.descending xor (cursor?.before == true)) "DESC" else "ASC"
        args += limit
        return SimpleSQLiteQuery(
            "SELECT p.id,p.name,p.nameKey,p.price,p.unit,p.stock,p.imageUri,c.name AS category " +
                "FROM products_table p LEFT JOIN categories_table c ON c.id=p.category_id " +
                where(conditions) + " ORDER BY p.$column $direction,p.id $direction LIMIT ?",
            args.toTypedArray()
        )
    }

    fun ids(): SimpleSQLiteQuery {
        val (conditions, args) = filters()
        val direction = if (query.descending) "DESC" else "ASC"
        return SimpleSQLiteQuery("SELECT p.id FROM products_table p " + where(conditions) +
            " ORDER BY p.$column $direction,p.id $direction", args.toTypedArray())
    }

    fun count(): SimpleSQLiteQuery {
        val (conditions, args) = filters()
        return SimpleSQLiteQuery("SELECT COUNT(*) FROM products_table p " + where(conditions), args.toTypedArray())
    }

    private fun filters(): Pair<MutableList<String>, MutableList<Any>> {
        val conditions = mutableListOf<String>()
        val args = mutableListOf<Any>()
        if (query.search.isNotEmpty()) {
            // Preserve SQLite LIKE matching (including existing wildcard behavior).
            conditions += "p.searchName LIKE ?"
            args += query.search + "%"
        }
        val categoryConditions = mutableListOf<String>()
        if (query.categoryNames.isNotEmpty()) {
            val names = query.categoryNames.sorted()
            categoryConditions += "p.category_id IN (SELECT id FROM categories_table WHERE name IN (${names.joinToString { "?" }}))"
            args.addAll(names)
        }
        if (query.includeUncategorized) categoryConditions += "p.category_id IS NULL"
        if (categoryConditions.isNotEmpty()) conditions += categoryConditions.joinToString(" OR ", "(", ")")
        return conditions to args
    }

    private fun where(conditions: List<String>) =
        if (conditions.isEmpty()) "" else conditions.joinToString(" AND ", "WHERE ")
}
