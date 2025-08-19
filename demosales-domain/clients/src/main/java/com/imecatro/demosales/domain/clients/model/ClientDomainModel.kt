package com.imecatro.demosales.domain.clients.model

/**
 * Data class representing a client in the domain layer.
 *
 * @param id Unique identifier for the client.
 * @param name The client's full name.
 * @param phoneNumber The client's phone number.
 * @param shipping The client's shipping address.
 * @param avatarUri The URL of the client's avatar image.
 */
data class ClientDomainModel(
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val shipping: String,
    val avatarUri: String?
)