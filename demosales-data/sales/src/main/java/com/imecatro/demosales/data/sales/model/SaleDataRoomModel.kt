package com.imecatro.demosales.data.sales.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales_table",
    indices = [Index("clientId")]
)
data class SaleDataRoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long? = null,// nullable for “walk-in” y safe delete
    val creationDateMillis: Long = 0,
    val status: String = "",
    val note: String = "",
    @Embedded(prefix = "totals_")
    val totals: SaleTotals? = null,
    @Embedded(prefix = "client_")
    val clientSnapshot: SaleClientSnapshot? = null
)

data class SaleTotals(
    @ColumnInfo(name = "subtotal")
    val subtotal: Double = 0.0,
    @ColumnInfo(name = "discount")
    val discount: Double = 0.0,
    @ColumnInfo(name = "extra")
    val extra: Double = 0.0,
    @ColumnInfo(name = "total")
    val total: Double = 0.0
)

/**
 * Represents a snapshot of client information at the time of a sale.
 * This is embedded within [SaleDataRoomModel] to preserve the client's details
 * as they were when the sale was made, even if the client's main record is updated or deleted later.
 *
 * @property name The name of the client at the time of the sale.
 * @property address The address of the client at the time of the sale, can be null.
 */
data class SaleClientSnapshot(
    @ColumnInfo(name = "name_at_sale")
    val name: String? = null,
    @ColumnInfo(name = "address_at_sale")
    val address: String? = null
)