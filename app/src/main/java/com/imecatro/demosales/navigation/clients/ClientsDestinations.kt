package com.imecatro.demosales.navigation.clients

import kotlinx.serialization.Serializable


/**
 * Defines the navigation destinations for the Clients feature.
 */
@Serializable
object ClientsList

/** Destination for adding a new client. */
@Serializable
object AddClient

/**
 * Destination for editing an existing client.
 * @property id The ID of the client to edit.
 */
@Serializable
data class EditClient(val id: Long)

/**
 * Destination for viewing client details.
 * @property id The ID of the client to view.
 */
@Serializable
data class ClientDetails(val id: Long)