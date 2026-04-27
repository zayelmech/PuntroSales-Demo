package com.imecatro.demosales.domain.clients.model

/**
 * Domain model representing a purchase record.
 *
 * @property id Unique identifier for the purchase record. Defaults to 0 for new records.
 * @property purchaseNumber A string identifier for the transaction (e.g., invoice number).
 * @property clientId The ID of the client who made the purchase.
 * @property description A brief summary about purchase.
 * @property amount The total monetary value of the transaction, including taxes and discounts.
 * @property date The timestamp when the purchase occurred, represented in milliseconds.
 */
data class PurchaseDomainModel(
    val id: Long = 0,
    val purchaseNumber: String,
    val clientId: Long,
    val description: String,
    val amount: Double,
    val date: Long
)