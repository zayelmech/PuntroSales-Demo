package com.imecatro.demosales.navigation.clients

import kotlinx.serialization.Serializable


@Serializable
object ClientsList

@Serializable
object AddClient

@Serializable
data class EditClient(val id: Long)

@Serializable
data class ClientDetails(val id: Long)